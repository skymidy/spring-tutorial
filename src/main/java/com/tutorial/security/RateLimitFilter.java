package com.tutorial.security;

import com.tutorial.Enum.AuthorityEnum;
import com.tutorial.model.entity.Authority;
import com.tutorial.model.entity.User;
import com.tutorial.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

@Slf4j
@Component
@Order(2) // After ApiKeyAuthenticationFilter (Order=1), before AuthorizationFilter
public class RateLimitFilter extends OncePerRequestFilter {


    @Qualifier("rateLimitTemplate")
    private final RedisTemplate<String, Long> redisTemplate;
    private final UserRepository userRepository;

    private static final String LUA_SCRIPT = """
            local key = KEYS[1]
            local limit = tonumber(ARGV[1])
            local window = 60 -- Fixed 60-second window
            local now = tonumber(ARGV[2])
            local cutoff = now - (window * 1000)
            
            redis.call('ZREMRANGEBYSCORE', key, 0, cutoff)
            local count = redis.call('ZCARD', key)
            
            if count < limit then
                redis.call('ZADD', key, now, now)
                redis.call('EXPIRE', key, window + 1)
                return count + 1
            end
            return -1
            """;

    private final DefaultRedisScript<Long> rateLimitScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

    public RateLimitFilter(RedisTemplate<String, Long> redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Skip unauthenticated requests and static resources
        if (auth == null || !auth.isAuthenticated() || isExcludedPath(request) || isAdmin(auth)) {
            chain.doFilter(request, response);
            return;
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ServletException("User not found"));

        long allowedRequests = user.getRateLimit();
        String redisKey = "ratelimit:user:" + user.getId();
        long now = Instant.now().toEpochMilli();

        // Execute with 150ms timeout
        Long requestsMade = redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(redisKey),
                String.valueOf(allowedRequests),
                String.valueOf(now)
        );
        log.info("User {} requestsMade {}", username, requestsMade);
        if (requestsMade == null || requestsMade == -1 || requestsMade > allowedRequests) {
            handleRateLimitExceeded(response, allowedRequests);
        }

        // Add standard rate limit headers
        response.setHeader("X-RateLimit-Limit", String.valueOf(allowedRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(allowedRequests - requestsMade));
        response.setHeader("X-RateLimit-Reset", "60");


        chain.doFilter(request, response);
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority(AuthorityEnum.ADMIN.name()));
    }

    private boolean isExcludedPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/") ||
                path.contains(".");
    }

    private void handleRateLimitExceeded(HttpServletResponse response, long limit) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", "60");
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", "0");

        String error = """
                        {"status":%d,"error":"Too Many Requests",
                        "message":"Rate limit exceeded. Maximum %d requests per minute.",
                        "retryAfter":60}
                        """.formatted(HttpStatus.TOO_MANY_REQUESTS.value(),limit);

        response.getWriter().write(error);
    }
}
