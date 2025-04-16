package com.assesment.company.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Spring Boot's auto-configuration will handle multipart configuration
    // based on the properties in application.properties
} 