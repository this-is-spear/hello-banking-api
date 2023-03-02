package numble.bankingapi.member.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberRepository;
import numble.bankingapi.member.dto.MemberResponse;
import numble.bankingapi.member.dto.RegisterCommand;
import numble.bankingapi.member.exception.NotExistMemberException;

@Service
@RequiredArgsConstructor
public class MemberApplicationService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public void registerMember(RegisterCommand registerCommand) {
		var encodedPassword = passwordEncoder.encode(registerCommand.password());
		validateEmailDuplication(registerCommand.email());
		var member = new Member(registerCommand.email(), registerCommand.name(), encodedPassword);
		memberRepository.save(member);
	}

	public MemberResponse getMember(String principal) {
		var member = memberRepository.findByEmail(principal).orElseThrow(NotExistMemberException::new);
		return new MemberResponse(member.getId(), member.getName());
	}

	private void validateEmailDuplication(String email) {
		if (memberRepository.findByEmail(email).isPresent()) {
			throw new DuplicateEmailException();
		}
	}
}
