package com.postit.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaFallbackController {

    // Explicit, targeted fallbacks for known client routes that were showing errors.
    @GetMapping({"/profile", "/profile/**", "/notifications", "/notifications/**"})
    public String fallback() {
        return "forward:/index.html";
    }
}

