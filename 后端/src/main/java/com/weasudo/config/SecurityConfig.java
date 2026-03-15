package com.weasudo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        // 需要认证的接口（精确路径，放在前面）
//                        .requestMatchers(
//                                "/users/info",
//                                "/users/password",
//                                "/articles/create",
//                                "/articles/*/thumb",
//                                "/articles/*/collect",
//                                "/comments/create"
//                        ).authenticated()
//                        // 公开接口（使用 Ant 通配符）
//                        .requestMatchers(
//                                "/users/login",
//                                "/users/register",
//                                "/users/*",
//                                //"/users/info",// ✅ 放行用户详情接口
//                                "/upload/avatar",
//                                "/uploads/**",
//                                "/articles/*",                   // 放行文章详情
//                                "/articles/author/**",
//                                "/articles/thumb",
//                                "/articles/time",
//                                "/comments/article/*"            // ✅ 修正为通配符
//                        ).permitAll()
//                        .anyRequest().authenticated()
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // 所有请求都放行
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
