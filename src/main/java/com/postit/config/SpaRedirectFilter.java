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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpaRedirectFilter extends OncePerRequestFilter {

    private static final String INDEX_PATH = "/index.html";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("SpaRedirectFilter: incoming request {} {}", method, path);

        // Only forward GET requests
        if (!"GET".equalsIgnoreCase(method)) {
            log.debug("SpaRedirectFilter: not a GET request, passing through: {} {}", method, path);
            filterChain.doFilter(request, response);
            return;
        }

        // If request is for API, static resource (contains a dot), or the index itself, let it pass
        if (path.startsWith("/api")) {
            log.debug("SpaRedirectFilter: API path, passing through: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        if (path.equals("/") || path.equals(INDEX_PATH)) {
            log.debug("SpaRedirectFilter: root/index path, passing through: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        if (path.contains(".")) {
            log.debug("SpaRedirectFilter: looks like a static resource (has dot), passing through: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        if (path.startsWith("/static") || path.startsWith("/favicon.ico") || path.startsWith("/manifest.json")
                || path.startsWith("/robots.txt") || path.startsWith("/asset-manifest.json")) {
            log.debug("SpaRedirectFilter: known static asset path, passing through: {}", path);
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
            // ignore and continue to serve index
            log.debug("SpaRedirectFilter: error checking resource existence for {}: {}", path, e.getMessage());
        }

        // Serve index.html directly from the classpath to avoid forwarding/dispatch problems
        try (InputStream indexStream = request.getServletContext().getResourceAsStream(INDEX_PATH)) {
            if (indexStream == null) {
                log.warn("SpaRedirectFilter: index.html not found on classpath; cannot serve {}. Passing through.", path);
                filterChain.doFilter(request, response);
                return;
            }

            log.info("SpaRedirectFilter: serving index.html for SPA route {}", path);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html;charset=UTF-8");

            // Copy bytes
            try (OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = indexStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }
            return;
        } catch (IOException e) {
            log.error("SpaRedirectFilter: error serving index.html for {}: {}", path, e.getMessage());
            filterChain.doFilter(request, response);
        }
    }
}
