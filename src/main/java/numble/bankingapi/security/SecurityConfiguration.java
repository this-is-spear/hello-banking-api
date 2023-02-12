package numble.bankingapi.security;

import static org.springframework.security.config.Customizer.*;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
				.anyRequest().denyAll()
			)
			.httpBasic(withDefaults())
			.formLogin(withDefaults());
		return http.build();
	}

	@Bean
	public UserDetailsService users() {
		UserDetails admin = User.withUserDetails(
			new User("admin", "password", List.of(new SimpleGrantedAuthority("ADMIN")))
		).build();
		return new InMemoryUserDetailsManager(admin);
	}
}
