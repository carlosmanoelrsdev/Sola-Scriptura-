package br.com.carlos.solascriptura.config;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.carlos.solascriptura.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final ObjectMapper objectMapper;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.objectMapper = objectMapper;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.httpBasic(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint((request, response, authException) ->
								writeError(response, request, HttpStatus.UNAUTHORIZED, "Authentication required"))
						.accessDeniedHandler((request, response, accessDeniedException) ->
								writeError(response, request, HttpStatus.FORBIDDEN, "Access denied")))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers("/api/auth/register", "/api/auth/login", "/api/health", "/api/bible/**").permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	private void writeError(
			HttpServletResponse response,
			HttpServletRequest request,
			HttpStatus status,
			String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("timestamp", OffsetDateTime.now().toString());
		payload.put("status", status.value());
		payload.put("error", status.getReasonPhrase());
		payload.put("message", message);
		payload.put("path", request.getRequestURI());

		objectMapper.writeValue(response.getOutputStream(), payload);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
