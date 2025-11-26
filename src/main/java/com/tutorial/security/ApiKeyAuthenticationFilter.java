package com.tutorial.security;

import com.tutorial.model.entity.User;
import com.tutorial.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(1)
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String X_API_KEY_HEADER = "X-API-KEY";

    private final UserRepository userRepository;

    public ApiKeyAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String apiKey = request.getHeader(X_API_KEY_HEADER);

        if (apiKey == null || apiKey.isBlank()) {
            chain.doFilter(request, response);
            return;
        }

        try {
            UserDetails userDetails = loadUserByApiKey(apiKey);

            ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(
                    userDetails,
                    apiKey,
                    userDetails.getAuthorities()
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }

    private UserDetails loadUserByApiKey(String apiKey) throws UsernameNotFoundException {
        User user = userRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid API key"));

        return getUserDetails(user);
    }

    private UserDetails getUserDetails(User user) {
        Set<SimpleGrantedAuthority> authorities = user.getAuthorities().stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority().name()))
                .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(!user.isEnabled())
                .build();
    }
}
