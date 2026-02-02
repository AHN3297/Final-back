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
            .securityMatcher("/api/**") // API만 이 체인으로 들어옴
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(requests -> {

                //  공개(permitAll)
                requests.requestMatchers("/api/universes/**").permitAll();
                requests.requestMatchers("/api/search").permitAll();

                requests.requestMatchers(HttpMethod.POST, "/api/auth/signUp", "/api/members/login").permitAll();
                requests.requestMatchers(HttpMethod.GET, "/api/members").permitAll();

                // 숏폼 조회만 공개
                requests.requestMatchers(HttpMethod.GET, "/api/shortforms/**").permitAll();

                // 음악/아티스트 조회 공개(원하면 나중에 authenticated로 바꾸기)
                requests.requestMatchers(HttpMethod.GET, "/api/music/**", "/api/artist/**").permitAll();

                //  관리자
                requests.requestMatchers(HttpMethod.GET, "/api/auth/admin/**").hasRole("ADMIN");
                requests.requestMatchers(HttpMethod.PATCH, "/api/auth/admin/**").hasRole("ADMIN");

                //  인증 필요(authenticated)
                requests.requestMatchers(HttpMethod.DELETE, "/api/members").authenticated();
                requests.requestMatchers(HttpMethod.PATCH, "/api/members").authenticated();
                requests.requestMatchers(HttpMethod.POST, "/api/members/logout").authenticated();
                requests.requestMatchers(HttpMethod.PUT, "/api/members").authenticated();

                requests.requestMatchers(HttpMethod.PUT, "/api/oauth/social/**").authenticated();
                requests.requestMatchers("/api/favorite/**").authenticated();
                requests.requestMatchers("/api/member/playList/**").authenticated();

                // 숏폼: 조회 외 전부 인증 필요
                requests.requestMatchers("/api/shortforms/**").authenticated();

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
