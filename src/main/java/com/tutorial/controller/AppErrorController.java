package com.tutorial.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class AppErrorController implements ErrorController {

  private final ErrorAttributes errorAttributes;

  public AppErrorController(ErrorAttributes errorAttributes) {
    this.errorAttributes = errorAttributes;
  }

  @RequestMapping("/error")
  public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
    ServletWebRequest webRequest = new ServletWebRequest(request);
    Map<String, Object> attrs = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
    int status = (int) attrs.getOrDefault("status", 500);
    return ResponseEntity.status(status).body(attrs);
  }
}
