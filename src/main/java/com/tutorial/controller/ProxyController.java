package com.tutorial.controller;

import com.tutorial.service.ProxyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @RequestMapping(value = "/{apiAlias}/**")
    public Mono<ResponseEntity<byte[]>> proxy(
            @PathVariable("apiAlias") String apiAlias,
            @RequestBody(required = false) byte[] body,
            @AuthenticationPrincipal UserDetails userDetails,
            ServerHttpRequest serverRequest) {

        return proxyService.proxyRequest(
                apiAlias,
                body,
                serverRequest,
                userDetails
        );
    }


}


