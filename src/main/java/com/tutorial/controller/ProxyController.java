package com.tutorial.controller;

import com.tutorial.service.ProxyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController("/api")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @RequestMapping(value = "/proxy/{apiAlias}/**")
    public Mono<ResponseEntity<byte[]>> proxy(
            @PathVariable("apiAlias") String apiAlias,
            @RequestBody(required = false) byte[] body,
            ServerHttpRequest serverRequest) {



        return proxyService.proxyRequest(
                        apiAlias,
                        body,
                        serverRequest
                )
                // Handle connection problems to target service
                .onErrorReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Connection to target service failed".getBytes()));
    }



}


