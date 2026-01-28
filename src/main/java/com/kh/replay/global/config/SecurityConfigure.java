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
				.oauth2Login(oauth -> oauth 
						.userInfoEndpoint(userInfo -> userInfo
								.userService(oAuth2UserService)
						)
						.successHandler(oAuth2SuccessHandler)
				)
				.authorizeHttpRequests(requests -> {
					// 1. 공통 비로그인 허용 경로 (유니버스, 소셜로그인, 검색)
					requests.requestMatchers("/api/universes/**", "/oauth2/**", "/login/**", "/oauth-callback", "/api/search").permitAll();
					requests.requestMatchers(HttpMethod.POST, "/api/auth/signUp", "/api/members/login").permitAll();
					requests.requestMatchers(HttpMethod.GET, "/api/members").permitAll();

					// 2. 회원 관련 설정 (탈퇴, 소셜 정보 수정 등)
					requests.requestMatchers(HttpMethod.DELETE, "/api/members").authenticated(); 
					requests.requestMatchers(HttpMethod.PATCH, "/api/members").authenticated();
					requests.requestMatchers(HttpMethod.PUT, "/api/oauth/social/**").authenticated();
					requests.requestMatchers("/api/favorite/**").authenticated();
					requests.requestMatchers("/api/member/playList/**").authenticated();
					
					// 3. 명시적 인증 필요 경로 (로그아웃, 내 정보 수정)
					requests.requestMatchers(HttpMethod.POST, "/api/members/logout").authenticated();
					requests.requestMatchers(HttpMethod.PUT, "/api/members").authenticated();
					
					// 4. [NEW] 숏폼(ShortForm) 관련 설정
					// (1) 조회(GET)는 누구나 가능 (목록, 검색, 상세)
					requests.requestMatchers(HttpMethod.GET, "/api/shortforms/**").permitAll();
					// (2) 그 외(작성, 수정, 삭제, 좋아요, 신고 등)는 로그인 필요
					requests.requestMatchers("/api/shortforms/**").authenticated();

					// 5. 음악/아티스트 상세조회 로그인 필요(test permiAll 앞단 로그인 구현 되면 authenticated()로 변경)
					requests.requestMatchers(HttpMethod.GET, "/api/music/**", "/api/artist/**").permitAll();
					
					// 6. 기타 기본 보안 정책 (가장 마지막에 위치)
					requests.requestMatchers(HttpMethod.GET).authenticated();
					requests.requestMatchers(HttpMethod.DELETE).authenticated();
					requests.requestMatchers(HttpMethod.PUT).permitAll();
				
					//7. 관리자 기능
					requests.requestMatchers(HttpMethod.GET,"/api/auth/admin/**").hasRole("ADMIN");
					requests.requestMatchers(HttpMethod.PATCH,"/api/auth/admin/**").hasRole("ADMIN");
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
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS"));
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