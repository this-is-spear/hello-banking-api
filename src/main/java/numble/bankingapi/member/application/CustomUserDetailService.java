package numble.bankingapi.member.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberRepository;
import numble.bankingapi.member.exception.NotExistMemberException;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(username).orElseThrow(NotExistMemberException::new);
		return new User(username, member.getPassword(), getAuthorities(member));
	}

	private List<SimpleGrantedAuthority> getAuthorities(Member member) {
		return member.getRoles().stream().map(SimpleGrantedAuthority::new).collect(
			Collectors.toList());
	}
}
