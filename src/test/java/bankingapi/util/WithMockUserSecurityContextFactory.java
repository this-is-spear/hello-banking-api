package bankingapi.util;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockUserSecurityContextFactory implements WithSecurityContextFactory<WithMockMember> {
	@Override
	public SecurityContext createSecurityContext(WithMockMember customUser) {
		var context = SecurityContextHolder.createEmptyContext();
		var principal = new User(customUser.username(), customUser.password(),
			List.of(new SimpleGrantedAuthority("MEMBER")));
		var auth = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
			principal.getAuthorities());
		context.setAuthentication(auth);
		return context;
	}
}
