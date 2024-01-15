package bankingapi.member.domain;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);
	}

	public Member findById(Long id) {
		return memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
	}

	public List<Member> findAllById(Iterable<Long> ids) {
		return memberRepository.findAllById(ids);
	}
}
