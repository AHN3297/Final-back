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

import com.kh.replay.global.config.filter.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigure {
	
	@Value("${instance.url}")
	private String instance;
	
	private final JwtFilter jwtFilter;
	// private final OAuth2UserProviderRouter oAuth2UserProviderRouter;
	// private final OAuth2SuccessHandler oAuth2SuccessHandler;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
	    return httpSecurity
	            .formLogin(AbstractHttpConfigurer::disable)
	            .csrf(AbstractHttpConfigurer::disable)
	            .cors(Customizer.withDefaults())
//	            oAuth2LoginSuccessHandler
	            .oauth2Login(oauth -> oauth.successHandler(null))
	            
	            .authorizeHttpRequests(requests -> {
	                requests.requestMatchers("/api/universes/**").permitAll();

	                requests.requestMatchers(HttpMethod.POST, "/api/member/playList/**").permitAll();
	                requests.requestMatchers(HttpMethod.PATCH, "/api/member/playList/**").permitAll();
	                requests.requestMatchers(HttpMethod.DELETE, "/api/member/playList/**").permitAll();
	                requests.requestMatchers(HttpMethod.GET, "/api/member/playList/**").permitAll();

	                requests.requestMatchers(HttpMethod.GET, "/api/members", "/api/search").permitAll();
	                requests.requestMatchers(HttpMethod.POST, "/api/auth/signUp", "/api/members/login").permitAll();
	                requests.requestMatchers(HttpMethod.DELETE, "/api/members").permitAll();
	                requests.requestMatchers(HttpMethod.PATCH, "/api/members").permitAll();

	                requests.requestMatchers(HttpMethod.POST, "/api/members/logout").authenticated();
	                requests.requestMatchers(HttpMethod.PUT, "/api/members").authenticated();

	                requests.requestMatchers(HttpMethod.PUT).permitAll(); 
	                
	                requests.requestMatchers(HttpMethod.GET).authenticated();
	                
	                requests.requestMatchers(HttpMethod.DELETE).authenticated();
	                
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