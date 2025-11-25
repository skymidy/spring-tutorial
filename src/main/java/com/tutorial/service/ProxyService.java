package com.tutorial.service;

import com.tutorial.Enum.ErrorCodesEnum;
import com.tutorial.exceptions.ProxyServiceException;
import com.tutorial.model.entity.ApiResource;
import com.tutorial.model.entity.RequestLog;
import com.tutorial.repository.ApiResourceRepository;
import com.tutorial.repository.RequestLogRepository;
import com.tutorial.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.lang.Nullable;

import java.util.Set;
import java.util.concurrent.TimeoutException;


@Service
@Slf4j
public class ProxyService {

    private static final Set<String> PROXY_AUTH_HEADERS = Set.of(
            HttpHeaders.AUTHORIZATION,
            "X-API-Key",
            "X-Gateway-Auth"
    );
    private static final String TARGET_AUTH_HEADER = "X-Target-Auth";

    private static final Set<String> HOP_BY_HOP_HEADERS;
    public static final int CACHE_TTL_SECONDS = 60;

    static {
        Set<String> s = new HashSet<>();
        Collections.addAll(s,
                "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
                "te", "trailers", "transfer-encoding", "upgrade", "host", "content-length");
        HOP_BY_HOP_HEADERS = Collections.unmodifiableSet(s);
    }

    private final ApiResourceRepository apiResourceRepository;
    private final RequestLogRepository requestLogRepository;
    private final WebClient webClient;
    private final AntPathMatcher pathMatcher;

    private final UserRepository userRepository;
    private final RedisTemplate<String, ResponseEntity<byte[]>> redisTemplate;
    ;

    public ProxyService(
            ApiResourceRepository apiResourceRepository,
            RequestLogRepository requestLogRepository,
            WebClient webClient,
            AntPathMatcher pathMatcher,
            UserRepository userRepository,
            RedisTemplate<String, ResponseEntity<byte[]>> redisTemplate) {
        this.apiResourceRepository = apiResourceRepository;
        this.requestLogRepository = requestLogRepository;
        this.webClient = webClient;
        this.pathMatcher = pathMatcher;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }


    public Mono<ResponseEntity<byte[]>> proxyRequest(
            String apiAlias,
            @Nullable byte[] body,
            ServerHttpRequest request,
            UserDetails userDetails) {
        //Mark time of proxy request process starting
        final long startTime = System.nanoTime();

        ApiResource resource = apiResourceRepository.findByNameAndIsEnabledTrue(apiAlias)
                .orElseThrow(() -> new ProxyServiceException(ErrorCodesEnum.API_RESOURCE_NOT_FOUND, apiAlias));

        String targetPath = extractRemainingPath(request);
        if (!isValidPath(targetPath)) {
            throw new ProxyServiceException(ErrorCodesEnum.INVALID_PATH, targetPath);
        }
        String queryString = request.getURI().getQuery();
        String sanitizedQuery = sanitizeQueryString(queryString);


        //Set up Http headers
        HttpMethod method = HttpMethod.valueOf(request.getMethod().name());
        HttpHeaders requestHeaders = request.getHeaders();
        HttpHeaders forwardHeaders = removeHopByHopHeaders(requestHeaders);
        removeProxyAuthHeaders(forwardHeaders);
        addForwardHeaders(forwardHeaders, request);
        addTargetServiceAuthHeaders(forwardHeaders, resource, request);

        //Build target URL
        String targetUrl = buildTargetUrl(resource, targetPath, sanitizedQuery);

        //Logging
        RequestLog requestLog = new RequestLog();
        requestLog.setRequestTimestamp(OffsetDateTime.now(ZoneOffset.UTC)); //TODO: Time zone settings
        requestLog.setUser(userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ProxyServiceException(ErrorCodesEnum.USER_NOT_FOUND)));
        requestLog.setApiResource(resource);
        requestLog.setAuthenticationType(resource.getAuthenticationType());
        requestLog.setHttpMethod(method.name());
        requestLog.setEndpoint(targetPath);

        String cacheKey = generateCacheKey(method, targetUrl, body);
        boolean shouldCacheRequest = shouldCacheRequest(method);
        if (shouldCacheRequest) {
            ResponseEntity<byte[]> cachedResponse = redisTemplate.opsForValue().getAndExpire(cacheKey, Duration.ofSeconds(CACHE_TTL_SECONDS));
            if (cachedResponse != null) {

                String hitsHeaderValue = cachedResponse.getHeaders().getFirst("X-Cache-Hits");
                int hitsValue = Integer.parseInt(hitsHeaderValue != null ? hitsHeaderValue : "0");
                cachedResponse.getHeaders().set("X-Cache-Hits", String.format("%d", hitsValue + 1));

                // TODO: Maybe set to container name or set server name in settings
                cachedResponse.getHeaders().set("X-Served-By", "It's me mario!!");

                return Mono.just(ResponseEntity.status(cachedResponse.getStatusCode())
                                .headers(cachedResponse.getHeaders())
                                .body(cachedResponse.getBody()))
                        .flatMap(responseEntity -> finalizeLogging(responseEntity, requestLog, startTime));
            }
        }

        return webClient.method(method)
                .uri(targetUrl)
                .headers(httpHeaders -> httpHeaders.addAll(forwardHeaders))
                .body(BodyInserters.fromValue(body != null ? body : new byte[0]))
                .retrieve()
                .toEntity(byte[].class)
                .map(this::normalizeSuccessfulResponse)
                .onErrorResume(WebClientResponseException.class, this::handleTargetServiceError)
                .onErrorResume(IOException.class, this::handleNetworkError)
                .map(responseEntity -> cacheResult(responseEntity, cacheKey, shouldCacheRequest))
                .flatMap(responseEntity -> finalizeLogging(responseEntity, requestLog, startTime))
                .onErrorResume(Mono::error);
    }

    private ResponseEntity<byte[]> cacheResult(
            ResponseEntity<byte[]> responseEntity,
            String cacheKey,
            boolean shouldCacheRequest
    ) {

        if (responseEntity.getStatusCode().is2xxSuccessful() && shouldCacheRequest) {
            ResponseEntity<byte[]> toCacheResponseEntity = new ResponseEntity<byte[]>(responseEntity.getBody(), responseEntity.getHeaders(), responseEntity.getStatusCode());
            toCacheResponseEntity.getHeaders().add("X-Cache", "HIT");
            toCacheResponseEntity.getHeaders().add("X-Cache-Hits", "0");

            redisTemplate.opsForValue()
                    .set(cacheKey, responseEntity, Duration.ofSeconds(CACHE_TTL_SECONDS));
        }
        return responseEntity;
    }

    private ResponseEntity<byte[]> normalizeSuccessfulResponse(ResponseEntity<byte[]> proxyResponse) {
        return ResponseEntity.status(proxyResponse.getStatusCode())
                .headers(removeHopByHopHeaders(proxyResponse.getHeaders()))
                .body(proxyResponse.getBody());
    }

    private Mono<ResponseEntity<byte[]>> handleTargetServiceError(WebClientResponseException ex) {
        return Mono.just(ResponseEntity.status(ex.getStatusCode())
                .headers(removeHopByHopHeaders(ex.getHeaders()))
                .body(ex.getResponseBodyAsByteArray()));
    }

    private Mono<ResponseEntity<byte[]>> handleNetworkError(Throwable throwable) {
        return Mono.just(ResponseEntity.status((throwable instanceof TimeoutException)
                        ? HttpStatus.GATEWAY_TIMEOUT.value()
                        : HttpStatus.BAD_GATEWAY.value())
                .contentType(MediaType.TEXT_PLAIN)
                .body(("Proxy error: " + throwable.getMessage()).getBytes()));
    }

    private Mono<ResponseEntity<byte[]>> finalizeLogging(
            ResponseEntity<byte[]> responseEntity,
            RequestLog requestLog,
            long startTime
    ) {
        long durationNanos = System.nanoTime() - startTime;
        double durationMs = durationNanos / 1_000_000.0;

        requestLog.setResponseTimeMs(Math.round(durationMs));
        requestLog.setResponseStatus(responseEntity.getStatusCode().value());
        requestLog.setResponseBodyType(
                responseEntity.hasBody() && responseEntity.getHeaders().getContentType() != null
                        ? responseEntity.getHeaders().getContentType().toString()
                        : "none"
        );

        try {
            requestLogRepository.save(requestLog);
        } catch (Exception ex) {
            throw new ProxyServiceException(ErrorCodesEnum.DB_ERROR, String.format("Logging failed but proxy succeeded: %s - %s", requestLog.getEndpoint(), ex.getMessage()));
        }

        return Mono.just(ResponseEntity.status(responseEntity.getStatusCode())
                .headers(responseEntity.getHeaders())
                .body(responseEntity.getBody()));
    }

    private void addForwardHeaders(HttpHeaders headers, ServerHttpRequest request) {
        // Add original client IP address
        String forwardedFor = request.getRemoteAddress() != null ?
                request.getRemoteAddress().getHostString() : "unknown";
        headers.add("X-Forwarded-For", forwardedFor);

        // Add original host information
        headers.add("X-Forwarded-Host", request.getHeaders().getFirst("host"));

        // Add original protocol (http/https)
        headers.add("X-Forwarded-Proto", request.getURI().getScheme());
    }

    private String buildTargetUrl(ApiResource resource, String path, String queryString) {

        String baseUrl = StringUtils.trimTrailingCharacter(resource.getBaseUrl(), '/');
        String targetPath = StringUtils.trimLeadingCharacter(path == null ? "" : path, '/');

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);

        if (!targetPath.isEmpty()) {
            builder.path("/").path(targetPath);
        }

        // Add query parameters if they exist
        if (queryString != null && !queryString.isEmpty()) {
            builder.query(queryString);
        }

        return builder.build(true).toUriString();
    }

    private HttpHeaders removeHopByHopHeaders(HttpHeaders headers) {
        HttpHeaders filteredHead = new HttpHeaders(headers);

        filteredHead.entrySet().removeIf((entry) ->
                HOP_BY_HOP_HEADERS.contains(entry.getKey().toLowerCase())
        );
        return filteredHead;
    }

    private void removeProxyAuthHeaders(HttpHeaders headers) {
        PROXY_AUTH_HEADERS.forEach(headers::remove);
        headers.remove(TARGET_AUTH_HEADER);
    }

    private void addTargetServiceAuthHeaders(HttpHeaders headers, ApiResource resource, ServerHttpRequest request) {
        if (resource.getTargetAuthHeader() == null) return;

        String targetAuthValue = request.getHeaders().getFirst(TARGET_AUTH_HEADER);
        if (targetAuthValue != null) {
            headers.set(resource.getTargetAuthHeader(), targetAuthValue);
        }
    }

    private String extractRemainingPath(ServerHttpRequest request) {
        String pathWithinMapping = request.getPath().value();

        String matchPattern = "/proxy/{apiAlias}/**";

        return pathMatcher.extractPathWithinPattern(matchPattern, pathWithinMapping);
    }

    private boolean isValidPath(String remainingPath) {
        if (remainingPath == null || remainingPath.isEmpty()) {
            return true;
        }
        // Prevent path traversal attacks
        return !remainingPath.contains("..");
    }

    private String sanitizeQueryString(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return queryString;
        }
        // TODO: OWASP Java
        return queryString;
    }

    private boolean shouldCacheRequest(HttpMethod method) {
        return method == HttpMethod.GET;
    }

    private String generateCacheKey(HttpMethod method, String targetUrl, @Nullable byte[] body) {
        StringBuilder key = new StringBuilder("cache:")
                .append(method.name())
                .append(":")
                .append(targetUrl.replace("/", "_"));

        // Only include body for requests with small payloads
        if (body != null && body.length > 0 && body.length < 1024) {
            String bodyHash = Base64.getEncoder().encodeToString(body);
            key.append(":b64:").append(bodyHash, 0, Math.min(16, bodyHash.length()));
        }
        return key.toString();
    }
}