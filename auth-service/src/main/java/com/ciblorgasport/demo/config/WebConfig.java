// src/main/java/com/ciblorgasport/demo/config/WebConfig.java
package com.ciblorgasport.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOrigins(
                "http://localhost:1002", 
                "http://localhost:5173",
                "https://bcfs-group-ciblorgasport-front.onrender.com" 
            )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
