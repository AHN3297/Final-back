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

    @Value("${instance.url}")
    private String instance; 

    private final JwtFilter jwtFilter;
    private final OAuth2LoginSuccessHandler oAuth2SuccessHandler;
    private final CustomOAuth2UserService oAuth2UserService;
  
    @Bean
    @Order
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

   
    @Bean
    @Order
    public SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/api/**")
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(requests -> {

                // === 공개 API ===
                
                // 인증 관련 공개
                requests.requestMatchers(HttpMethod.POST, "/api/auth/signUp").permitAll();
                requests.requestMatchers(HttpMethod.POST, "/api/members/login").permitAll();
                
                // Universe, Search 공개
                requests.requestMatchers("/api/universes/**").permitAll();
                requests.requestMatchers("/api/search").permitAll();

                // 숏폼 조회만 공개
                requests.requestMatchers(HttpMethod.GET, "/api/shortforms/**").permitAll();

                // 음악/아티스트 조회 공개
                requests.requestMatchers(HttpMethod.GET, "/api/music/**").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/artist/**").permitAll();

                // === 관리자 전용 ===
                requests.requestMatchers("/api/auth/admin/**").hasRole("ADMIN");

                // === 인증 필요 ===
                
                // 회원 정보 관련
                requests.requestMatchers(HttpMethod.GET, "/api/members").authenticated();  // 회원 정보 조회
                requests.requestMatchers(HttpMethod.PATCH, "/api/members").authenticated(); // 회원 정보 수정
                requests.requestMatchers(HttpMethod.PUT, "/api/members").authenticated();   // 비밀번호 변경
                requests.requestMatchers(HttpMethod.DELETE, "/api/members").authenticated(); // 회원 탈퇴
                requests.requestMatchers(HttpMethod.POST, "/api/members/logout").authenticated();
                
                // OAuth 관련
                requests.requestMatchers("/api/oauth/**").authenticated();
                
                // 좋아요, 플레이리스트
                requests.requestMatchers("/api/favorite/**").authenticated();
                requests.requestMatchers("/api/member/playList/**").authenticated();

                // 숏폼: 조회 외 전부 인증 필요
                requests.requestMatchers(HttpMethod.POST, "/api/shortforms/**").authenticated();
                requests.requestMatchers(HttpMethod.PUT, "/api/shortforms/**").authenticated();
                requests.requestMatchers(HttpMethod.PATCH, "/api/shortforms/**").authenticated();
                requests.requestMatchers(HttpMethod.DELETE, "/api/shortforms/**").authenticated();

                // 나머지 모든 API는 인증 필요
                requests.anyRequest().authenticated();
            })
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // instance 예: http://localhost:5173
        configuration.setAllowedOrigins(List.of(instance));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
