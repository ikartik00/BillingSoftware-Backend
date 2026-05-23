package in.kr.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import in.kr.main.filter.JwtFilter;
import in.kr.main.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtFilter jwtFilter;
	
	private final AppUserDetailsService service;
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**") // sab endpoints ke liye
						.allowedOrigins("http://localhost:5173") // frontend URL
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http.cors(Customizer.withDefaults());
		http.csrf(customizer->customizer.disable());
		http.authorizeHttpRequests(request->request
				.requestMatchers("/login", "/encode", "/register").permitAll()
				.requestMatchers("/categories", "/items", "/orders", "/payments", "/dashboard", "/user").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated());
		http.httpBasic(httpBasic -> httpBasic.disable());
		http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		//agar hum addfilterbefore nhi lagate to spring ko pata nhi chalega koi aane wali request authenticated hai ya nahi isiliye jab request aati hai to jwtfilter sabse pehle usme se token nikalta hai or usko jwtfilter se verify karta hai or use securitycontext mai set kar deta hai ab agli bar user request karega to sirf token verify hoga or usernamepasswordauthenticatiionfilter ko pata chalge ki user authenticated hai mai skip kar deta hu.
		return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(service);
		provider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(provider);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}
	
	
}
