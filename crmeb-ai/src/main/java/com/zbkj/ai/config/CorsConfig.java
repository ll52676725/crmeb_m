package com.zbkj.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源
        config.addAllowedOrigin("*");
        // 允许所有方法
        config.addAllowedMethod("*");
        // 允许所有头
        config.addAllowedHeader("*");
        // 允许凭证
        config.setAllowCredentials(true);
        // 注册配置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}