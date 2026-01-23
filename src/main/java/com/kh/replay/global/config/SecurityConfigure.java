package com.kh.replay.global.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
	 private final CustomOAuth2UserService oAuth2UserService;
	 private final OAuth2LoginSuccessHandler oAuth2SuccessHandler;
	
	 @Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
			return httpSecurity
					.formLogin(AbstractHttpConfigurer::disable)
					.csrf(AbstractHttpConfigurer::disable)
					.cors(Customizer.withDefaults())
					// OAuth2 설정 유지 (팀원 작업분)
					.oauth2Login(oauth -> oauth
							.userInfoEndpoint(userInfo -> userInfo
									.userService(oAuth2UserService)
							)
							.successHandler(oAuth2SuccessHandler)
					)
					.authorizeHttpRequests(requests -> {
						// 1. 공통 비로그인 허용 경로
						requests.requestMatchers("/api/universes/**", "/oauth2/**", "/login/**", "/oauth-callback", "/api/search").permitAll();
						requests.requestMatchers(HttpMethod.POST, "/api/auth/signUp", "/api/members/login").permitAll();
						requests.requestMatchers(HttpMethod.GET, "/api/members").permitAll();
						
						// 2. 플레이리스트 관련 허용 (팀원 작업분)
						requests.requestMatchers("/api/member/playList/**").permitAll();

						// 3. 좋아요(Likes) 관련 설정 (사용자님 작업분 - 인증 필요)
						// POST, DELETE 등 모든 /api/likes/** 요청은 인증된 사용자만 가능
						requests.requestMatchers("/api/likes/**").authenticated();

						// 4. 회원 관련 설정
						requests.requestMatchers(HttpMethod.DELETE, "/api/members").permitAll(); // 회원탈퇴 등
						requests.requestMatchers(HttpMethod.PATCH, "/api/members").permitAll();
						requests.requestMatchers(HttpMethod.PUT, "/api/oauth/social/**").permitAll();
						
						// 5. 로그인 필수 경로
						requests.requestMatchers(HttpMethod.POST, "/api/members/logout").authenticated();
						requests.requestMatchers(HttpMethod.PUT, "/api/members").authenticated();
						
						// 6. 기타 기본 설정
						requests.requestMatchers(HttpMethod.GET).authenticated();
						requests.requestMatchers(HttpMethod.DELETE).authenticated();
						requests.requestMatchers(HttpMethod.PUT).permitAll();
					})
					.exceptionHandling(ex -> 
						ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
					)
					.sessionManagement(manager -> 
						manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					)
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
					.build();
		}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(instance));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-type"));
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