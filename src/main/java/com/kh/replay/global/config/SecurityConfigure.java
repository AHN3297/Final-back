package com.kh.replay.global.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kh.replay.auth.oauth.model.sevice.CustomOAuth2UserService;
import com.kh.replay.global.config.filter.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigure {

    @Value("${front.base-url}")
    private String frontBaseUrl;

    private final JwtFilter jwtFilter;
    private final OAuth2LoginSuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService oAuth2UserService;

    /**
     * ===============================
     * OAuth2 로그인 전용 체인
     * ===============================
     */
    @Bean
    @Order(1)
    public SecurityFilterChain oauth2Chain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/oauth2/**", "/login/oauth2/**", "/login/**")
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .build();
    }

    /**
     * ===============================
     * API (JWT) 전용 체인
     * ===============================
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**", "/ws-chat/**")
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(requests -> {

                // === WebSocket ===
                requests.requestMatchers("/ws-chat/**").permitAll();

                // === 공개 API ===
                requests.requestMatchers(HttpMethod.POST, "/api/auth/signUp").permitAll();
                requests.requestMatchers(HttpMethod.POST, "/api/members/login").permitAll();
                requests.requestMatchers("/api/universes/**").permitAll();
                requests.requestMatchers("/api/search").permitAll();
                requests.requestMatchers("/api/oauth-callback").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/shortforms/**").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/music/**").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/artist/**").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/news").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/members/genres").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/admin/notices").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/admin/notices/*").permitAll();

                // === 관리자 ===
                requests.requestMatchers("/api/auth/admin/**").hasRole("ADMIN");

                // === 인증 필요 ===
                // 회원 관련
                requests.requestMatchers(HttpMethod.GET, "/api/members").authenticated();
                requests.requestMatchers(HttpMethod.PATCH, "/api/members").authenticated();
                requests.requestMatchers(HttpMethod.PUT, "/api/members").authenticated();
                requests.requestMatchers(HttpMethod.DELETE, "/api/members").authenticated();
                requests.requestMatchers(HttpMethod.POST, "/api/members/logout").authenticated();

                // OAuth 및 즐겨찾기
                requests.requestMatchers("/api/oauth/**").authenticated();
                requests.requestMatchers(HttpMethod.PUT, "/api/oauth/social/**").authenticated();
                requests.requestMatchers("/api/favorite/**").authenticated();
                requests.requestMatchers("/api/member/playList/**").authenticated();

                // 숏폼 작성/수정/삭제
                requests.requestMatchers(HttpMethod.POST, "/api/shortforms/**").authenticated();
                requests.requestMatchers(HttpMethod.PUT, "/api/shortforms/**").authenticated();
                requests.requestMatchers(HttpMethod.PATCH, "/api/shortforms/**").authenticated();
                requests.requestMatchers(HttpMethod.DELETE, "/api/shortforms/**").authenticated();

                requests.anyRequest().authenticated();
            })
            .build();
    }

    /**
     * ===============================
     * CORS 설정
     * ===============================
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(frontBaseUrl, "http://localhost:5173"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        );
        configuration.setAllowedHeaders(
            Arrays.asList("Authorization", "Content-Type", "X-Requested-With")
        );
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}