package com.kaiwaru.ticketing.config;

import com.kaiwaru.ticketing.interceptor.VisitorTrackingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private VisitorTrackingInterceptor visitorTrackingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(visitorTrackingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/**", "/admin/**", "/static/**", 
                                   "/css/**", "/js/**", "/images/**", "/favicon.ico");
    }
}