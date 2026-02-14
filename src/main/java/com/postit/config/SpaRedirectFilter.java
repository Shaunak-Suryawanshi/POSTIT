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
import java.net.MalformedURLException;
import java.net.URL;

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

        // If the servlet context actually has a resource at this path, don't forward.
        try {
            URL existing = request.getServletContext().getResource(path);
            if (existing != null) {
                log.debug("SpaRedirectFilter: resource exists for {}, letting it pass ({}).", path, existing);
                filterChain.doFilter(request, response);
                return;
            }
        } catch (MalformedURLException e) {
            // ignore and continue to forward
            log.debug("SpaRedirectFilter: error checking resource existence for {}: {}", path, e.getMessage());
        }

        // If index.html is missing, don't forward to avoid looping — pass through so container can handle
        try {
            URL indexUrl = request.getServletContext().getResource("/index.html");
            if (indexUrl == null) {
                log.warn("SpaRedirectFilter: index.html not found on classpath; cannot forward {}. Passing through.", path);
                filterChain.doFilter(request, response);
                return;
            }
        } catch (MalformedURLException e) {
            log.warn("SpaRedirectFilter: error checking index.html existence: {}. Passing through.", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Forward other GET requests to index.html so the client-side router can handle them
        log.info("SpaRedirectFilter: forwarding SPA route {} to /index.html", path);
        request.getRequestDispatcher("/index.html").forward(request, response);
    }
}
