package com.kh.replay.global.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kh.replay.global.config.filter.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigure {
	
	@Value("${instance.url}")
	private String instance;
	
	private final JwtFilter jwtFilter;
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
		return httpSecurity
				.formLogin(AbstractHttpConfigurer::disable)
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests(requests -> {
					
					 // 공지사항: 조회는 전체 허용 / 등록은 ADMIN만
	                requests.requestMatchers(HttpMethod.GET,  "/api/admin/notices/**").permitAll();
	                requests.requestMatchers(HttpMethod.POST, "/api/admin/notices/**").hasRole("ADMIN");
					
					
	                // 유니버스 조회는 전체 허용
	                requests.requestMatchers(HttpMethod.GET, "/api/universes/**").permitAll();

	                // 유니버스 수정/등록/삭제만 로그인 필요
	                requests.requestMatchers(HttpMethod.POST,   "/api/universes/**").authenticated();
	                requests.requestMatchers(HttpMethod.PUT,    "/api/universes/**").authenticated();
	                requests.requestMatchers(HttpMethod.PATCH,  "/api/universes/**").authenticated();
	                requests.requestMatchers(HttpMethod.DELETE, "/api/universes/**").authenticated();

					
					// 로그인필요(POST)테스트 플레이 리스트
					requests.requestMatchers(HttpMethod.POST,"/api/member/playList/**").permitAll();
					// 로그인 필요(PATCH)테스트 플레이 리스트
					requests.requestMatchers(HttpMethod.PATCH,"/api/member/playList/**").permitAll();
					// 로그인 필요(DELETE)테스트 플레이 리스트
					requests.requestMatchers(HttpMethod.DELETE,"/api/member/playList/**").permitAll();
					// 로그인 필요(GET)테스트 플레이 리스트
					requests.requestMatchers(HttpMethod.GET,"/api/member/playList/**").permitAll();
					
					// 비로그인 허용
					requests.requestMatchers(HttpMethod.GET,"/api/members", "/api/search").permitAll();
					// 비로그인 허용(POST)
					requests.requestMatchers(HttpMethod.POST,"/api/auth/signUp","/api/members/login").permitAll();
					requests.requestMatchers(HttpMethod.DELETE,"/api/members").permitAll();
					
					requests.requestMatchers(HttpMethod.PUT).permitAll();
					requests.requestMatchers(HttpMethod.PATCH,"/api/members").permitAll();
				
					// 로그인 필요(GET)
					
					requests.requestMatchers(HttpMethod.GET).authenticated();
					// 로그인 필요(POST)
					requests.requestMatchers(HttpMethod.POST,"/api/members/logout").authenticated();
					// 로그인 필요(PUT)
					requests.requestMatchers(HttpMethod.PUT,"/api/members").authenticated();
					// 로그인 필요(DELETE)
					requests.requestMatchers(HttpMethod.DELETE).authenticated();
					
					
//					// 관리자
//					requests.requestMatchers(HttpMethod.GET).hasAuthority("");
//					requests.requestMatchers(HttpMethod.POST).hasAuthority("");
//					requests.requestMatchers(HttpMethod.PUT).hasAuthority("");
//					requests.requestMatchers(HttpMethod.DELETE).hasAuthority("");


				})
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
		        .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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