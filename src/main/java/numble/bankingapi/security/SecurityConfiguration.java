package numble.bankingapi.security;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/hello").permitAll()
				.requestMatchers("/docs/**").permitAll()
				.requestMatchers("/account/**").authenticated()
				.anyRequest().denyAll()
			)
			.httpBasic(withDefaults())
			.formLogin(withDefaults());
		return http.build();
	}

	@Bean
	@SuppressWarnings("all")
	public UserDetailsService users() {
		User.UserBuilder users = User.withDefaultPasswordEncoder();
		UserDetails admin = users
			.username("admin")
			.password("password")
			.roles("USER", "ADMIN")
			.build();
		return new InMemoryUserDetailsManager(admin);
	}
}
