package com.postit.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 0;

        String uri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (uri == null) {
            uri = request.getRequestURI();
        }

        // If it's a 404 for a non-API path and doesn't look like a static asset (no dot), forward to index.html
        if (status == HttpStatus.NOT_FOUND.value()) {
            if (!uri.startsWith("/api") && !uri.contains(".")) {
                return "forward:/index.html";
            }
        }

        // For other errors, let the container handle (could be improved to show a custom error page)
        return "forward:/error";
    }
}
