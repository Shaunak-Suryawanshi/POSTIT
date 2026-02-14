package com.postit.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    // Keep only a safe root mapping; nested SPA routes are handled by SpaRedirectFilter
    @RequestMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
