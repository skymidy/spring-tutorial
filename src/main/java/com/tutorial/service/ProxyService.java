package com.tutorial.service;

import com.tutorial.model.entity.ApiResource;
import com.tutorial.repository.ApiResourceRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Service
public class ProxyService {

    private static final Set<String> HOP_BY_HOP_HEADERS;

    static {
        Set<String> s = new HashSet<>();
        Collections.addAll(s,
                "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
                "te", "trailers", "transfer-encoding", "upgrade", "host", "content-length");
        HOP_BY_HOP_HEADERS = Collections.unmodifiableSet(s);
    }

    private final ApiResourceRepository apiResourceRepository;
    private final WebClient webClient;
    private final AntPathMatcher pathMatcher;

    public ProxyService(ApiResourceRepository apiResourceRepository, WebClient webClient, AntPathMatcher pathMatcher) {
        this.apiResourceRepository = apiResourceRepository;
        this.webClient = webClient;
        this.pathMatcher = pathMatcher;
    }


    public Mono<ResponseEntity<byte[]>> proxyRequest(
            String apiAlias,
            byte[] body,
            ServerHttpRequest request) {


        String queryString = request.getURI().getQuery();
        String targetPath = extractRemainingPath(request);
        HttpMethod method = HttpMethod.valueOf(request.getMethod().name());
        HttpHeaders requestHeaders = request.getHeaders();

        ApiResource resource = apiResourceRepository.findByNameAndIsEnabledTrue(apiAlias)
                .orElseThrow(() -> new IllegalArgumentException("API resource not found or disabled: " + apiAlias));

        if (!isValidPath(targetPath)) {
            return Mono.error(new IllegalArgumentException("Invalid path"));
        }

        
        String sanitizedQuery = sanitizeQueryString(queryString);
        String targetUrl = buildTargetUrl(resource, targetPath, sanitizedQuery);


        HttpHeaders headers = removeHopByHopHeaders(requestHeaders);
        addForwardHeaders(headers, request);

        return webClient
                .method(method)
                .uri(targetUrl)
                .headers(httpHeaders -> {
                    httpHeaders.addAll(headers);
                })
                .bodyValue(body != null ? body : new byte[0])
                .retrieve()
                .toEntity(byte[].class)
                .map(proxyResponse -> {

                    HttpHeaders filteredResponseHeaders = removeHopByHopHeaders(proxyResponse.getHeaders());

                    return ResponseEntity.status(proxyResponse.getStatusCode())
                            .headers(filteredResponseHeaders)
                            .body(proxyResponse.getBody());
                })

                .onErrorResume(WebClientResponseException.class, ex -> {
                    // Log the error for debugging
                    System.out.println("Target service returned error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());

                    // Create a response with the same status code and error body from target service
                    return Mono.just(ResponseEntity.status(ex.getStatusCode())
                            .headers(removeHopByHopHeaders(ex.getHeaders()))
                            .body(ex.getResponseBodyAsByteArray()));
                });
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

    public String buildTargetUrl(ApiResource resource, String path, String queryString) {

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

    private String extractRemainingPath(ServerHttpRequest request) {
        String pathWithinMapping = request.getPath().value();

        String matchPattern = "/proxy/{apiAlias}/**";

        return pathMatcher.extractPathWithinPattern(matchPattern, pathWithinMapping);
    }


    public boolean isValidPath(String remainingPath) {
        if (remainingPath == null || remainingPath.isEmpty()) {
            return true;
        }
        // Prevent path traversal attacks
        return !remainingPath.contains("..");
    }

    public String sanitizeQueryString(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return queryString;
        }
        // TODO:
        return queryString;
    }
}


