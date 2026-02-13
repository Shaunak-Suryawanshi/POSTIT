package com.postit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class PostitApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostitApplication.class, args);
    }
}
