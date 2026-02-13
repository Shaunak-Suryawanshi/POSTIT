package com.postit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {
    // Kafka configuration is provided via application.yml
    // Spring Boot auto-configuration handles the rest
}

