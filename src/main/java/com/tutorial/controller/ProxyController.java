package com.tutorial.controller;

import com.tutorial.model.entity.ApiResource;
import com.tutorial.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@RestController
public class ProxyController {

    private static final Set<String> HOP_BY_HOP_HEADERS;
    static {
        Set<String> s = new HashSet<>();
        Collections.addAll(s,
                "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
                "te", "trailers", "transfer-encoding", "upgrade", "host", "content-length");
        HOP_BY_HOP_HEADERS = Collections.unmodifiableSet(s);
    }

    private final RestTemplate restTemplate;
    private final ProxyService proxyService;

    public ProxyController(RestTemplate restTemplate, ProxyService proxyService) {
        this.restTemplate = restTemplate;
        this.proxyService = proxyService;
    }

    @RequestMapping(value = "/proxy/{apiAlias}/**")
    public ResponseEntity<byte[]> proxy(
            @PathVariable("apiAlias") String apiAlias,
            @RequestBody(required = false) byte[] body,
            HttpServletRequest request) {

        String remaining = extractRemainingPath(request);
        ApiResource resource = proxyService.getResourceByAlias(apiAlias);
        String targetUrl = proxyService.buildTargetUrl(resource, remaining, request.getQueryString());

        HttpHeaders headers = extractRequestHeaders(request);
        addForwardHeaders(headers, request);

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> resp = restTemplate.exchange(targetUrl, method, httpEntity, byte[].class);

        HttpHeaders responseHeaders = filterResponseHeaders(resp.getHeaders());
        return ResponseEntity.status(resp.getStatusCode())
                .headers(responseHeaders)
                .body(resp.getBody());
    }

    private String extractRemainingPath(HttpServletRequest request) {
        String pathWithinMapping = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        AntPathMatcher apm = new AntPathMatcher();
        return apm.extractPathWithinPattern(bestMatchPattern, pathWithinMapping);
    }

    private HttpHeaders extractRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (name == null) continue;
            String lower = name.toLowerCase();
            if (HOP_BY_HOP_HEADERS.contains(lower)) continue;

            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                headers.add(name, values.nextElement());
            }
        }
        return headers;
    }

    private void addForwardHeaders(HttpHeaders headers, HttpServletRequest request) {
        String forwardedFor = request.getRemoteAddr();
        headers.add("X-Forwarded-For", forwardedFor);
        headers.add("X-Forwarded-Host", request.getServerName());
        headers.add("X-Forwarded-Proto", request.getScheme());
    }

    private HttpHeaders filterResponseHeaders(HttpHeaders original) {
        HttpHeaders filtered = new HttpHeaders();
        original.forEach((name, values) -> {
            String lower = name.toLowerCase();
            if (HOP_BY_HOP_HEADERS.contains(lower)) return;
            for (String v : values) {
                filtered.add(name, v);
            }
        });
        return filtered;
    }
}


