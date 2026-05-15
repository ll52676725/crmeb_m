package com.zbkj.admin.config;

import com.zbkj.admin.filter.JwtAuthenticationTokenFilter;
import com.zbkj.admin.manager.AuthenticationEntryPointImpl;
import com.zbkj.admin.manager.CustomAccessDeniedHandler;
import com.zbkj.admin.manager.CustomAuthenticationProvider;
import com.zbkj.common.constants.Constants;
import com.zbkj.service.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;

/**
 * Security配置（Spring Security 6 风格）
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    /**
     * 跨域过滤器
     */
    @Autowired
    private CorsFilter corsFilter;

    /**
     * token认证过滤器
     */
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    /**
     * 认证失败处理类
     */
    @Bean
    public AuthenticationEntryPointImpl unauthorizedHandler() {
        return new AuthenticationEntryPointImpl();
    }

    /**
     * 鉴权失败处理类
     */
    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    /**
     * 自定义认证提供者
     */
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(new UserDetailServiceImpl());
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }

    /**
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CRSF禁用，因为不使用session
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            // 认证失败处理类
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedHandler())
                .accessDeniedHandler(accessDeniedHandler())
            )
            // 基于token，所以不需要session
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // 过滤请求
            .authorizeHttpRequests(auth -> auth
                // 对于登录login 验证码captchaImage 和其他放行的目录 允许匿名访问
                .requestMatchers("/api/admin/login", "/api/admin/validate/code/get").permitAll()
                .requestMatchers("/api/admin/getLoginPic").permitAll()
                // 放行资源路径
                .requestMatchers("/" + Constants.UPLOAD_TYPE_IMAGE + "/**").anonymous()
                // 放行图片、文件上传
                .requestMatchers("/api/admin/upload/image").permitAll()
                .requestMatchers("/api/admin/upload/file").permitAll()
                // 代码生成器
                .requestMatchers("/api/codegen/code").permitAll()
                // 静态资源
                .requestMatchers(HttpMethod.GET,
                    "/*.html",
                    "/**/*.html",
                    "/**/*.css",
                    "/**/*.js"
                ).permitAll()
                .requestMatchers("/profile/**").anonymous()
                .requestMatchers("/common/download**").anonymous()
                .requestMatchers("/common/download/resource**").anonymous()
                // Swagger文档（后续将改为 SpringDoc 路径）
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/doc.html").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/*/api-docs").anonymous()
                .requestMatchers("/druid/**").anonymous()
                .requestMatchers("/captcha/get", "/captcha/check").anonymous()
                .requestMatchers("/api/admin/payment/callback/**").anonymous()
                .requestMatchers("/api/public/**").anonymous()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
            )
            // 防止iframe 造成跨域
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        // 添加JWT filter
        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // 添加CORS filter
        http.addFilterBefore(corsFilter, JwtAuthenticationTokenFilter.class);
        http.addFilterBefore(corsFilter, LogoutFilter.class);

        return http.build();
    }
}
