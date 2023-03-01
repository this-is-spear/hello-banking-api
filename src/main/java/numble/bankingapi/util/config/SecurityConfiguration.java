package numble.bankingapi.util.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	private final UserDetailsService userDetailsService;

	@Bean
	@Primary
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.userDetailsService(userDetailsService);
		http.csrf().disable();

		http
			.httpBasic(withDefaults())
			.formLogin()
			.successHandler((request, response, authentication) -> response.sendRedirect("/hello"))
			.and()
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/hello").permitAll()
				.requestMatchers("/docs/index.html").permitAll()
				.requestMatchers("/members/register").anonymous()
				.requestMatchers("/login").anonymous()
				.requestMatchers("/account/**").authenticated()
				.requestMatchers("/members/**").authenticated()
			);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
