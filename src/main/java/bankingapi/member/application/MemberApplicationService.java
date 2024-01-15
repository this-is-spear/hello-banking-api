package bankingapi.member.application;

import bankingapi.member.domain.Member;
import bankingapi.member.domain.MemberRepository;
import bankingapi.member.dto.MemberResponse;
import bankingapi.member.dto.RegisterCommand;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import bankingapi.member.exception.NotExistMemberException;

@Service
@RequiredArgsConstructor
public class MemberApplicationService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public void registerMember(RegisterCommand registerCommand) {
		final var encodedPassword = passwordEncoder.encode(registerCommand.password());
		validateEmailDuplication(registerCommand.email());
		final var member = new Member(registerCommand.email(), registerCommand.name(), encodedPassword);
		memberRepository.save(member);
	}

	public MemberResponse getMember(String principal) {
		final var member = memberRepository.findByEmail(principal).orElseThrow(NotExistMemberException::new);
		return new MemberResponse(member.getId(), member.getName());
	}

	private void validateEmailDuplication(String email) {
		if (memberRepository.findByEmail(email).isPresent()) {
			throw new DuplicateEmailException();
		}
	}
}
