package com.example.swp391_d01_g3.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages"); // Base name of the properties files
        source.setDefaultEncoding("UTF-8"); // Ensure UTF-8 for Vietnamese characters
        return source;
    }
}