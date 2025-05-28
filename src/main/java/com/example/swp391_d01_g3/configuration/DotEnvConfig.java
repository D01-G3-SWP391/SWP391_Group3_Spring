package com.example.swp391_d01_g3.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConfig {

    static {
        // Load .env file before Spring starts
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        if (dotenv != null) {
            dotenv.entries().forEach(entry -> {
                System.setProperty(entry.getKey(), entry.getValue());
                System.out.println("Loaded env var: " + entry.getKey() + " = " + entry.getValue());
            });
        } else {
            System.out.println("No .env file found");
        }
    }
}