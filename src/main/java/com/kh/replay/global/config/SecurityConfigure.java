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
						requests.requestMatchers(HttpMethod.POST, "/api/auth/**", "/api/members/login").permitAll();
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
						
						// 음악/아티스트 상세조회 로그인필요
						requests.requestMatchers(HttpMethod.GET, "/api/music/**", "/api/artist/**").authenticated();
						
						// 5. 기타 기본 보안 정책
						requests.requestMatchers(HttpMethod.GET).authenticated();
						requests.requestMatchers(HttpMethod.DELETE).authenticated();
						requests.requestMatchers(HttpMethod.PUT).permitAll();
						
						//6. 관리자 
						requests.requestMatchers("/admin/**").hasRole("ADMIN");
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