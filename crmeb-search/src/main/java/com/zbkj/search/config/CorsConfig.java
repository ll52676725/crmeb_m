package com.zbkj.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * @author CRMEB
 * @since 2024-05-20
 */
@Configuration
public class CorsConfig {

    /**
     * 允许跨域请求的过滤器
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有来源
        config.addAllowedOrigin("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 允许携带凭证
        config.setAllowCredentials(true);

        // 对所有路径应用跨域配置
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
