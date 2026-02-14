package com.postit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpaRedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Only forward GET requests
        if (!"GET".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // If request is for API, static resource (contains a dot), or the index itself, let it pass
        if (path.startsWith("/api") || path.equals("/") || path.equals("/index.html") || path.contains(".")
                || path.startsWith("/static") || path.startsWith("/favicon.ico") || path.startsWith("/manifest.json")
                || path.startsWith("/robots.txt") || path.startsWith("/asset-manifest.json")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Forward other GET requests to index.html so the client-side router can handle them
        log.debug("Forwarding SPA route {} to /index.html", path);
        request.getRequestDispatcher("/index.html").forward(request, response);
    }
}

