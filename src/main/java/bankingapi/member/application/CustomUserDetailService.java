package bankingapi.member.application;

import java.util.List;
import java.util.stream.Collectors;

import bankingapi.member.domain.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import bankingapi.member.domain.MemberRepository;
import bankingapi.member.exception.NotExistMemberException;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final var member = memberRepository.findByEmail(username).orElseThrow(NotExistMemberException::new);
		return new User(username, member.getPassword(), getAuthorities(member));
	}

	private List<SimpleGrantedAuthority> getAuthorities(Member member) {
		return member.getRoles().stream().map(SimpleGrantedAuthority::new).collect(
			Collectors.toList());
	}
}
