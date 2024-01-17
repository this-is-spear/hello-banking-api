package bankingapi.member.application;

import static bankingapi.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import bankingapi.member.domain.Member;
import bankingapi.member.domain.MemberRepository;
import bankingapi.member.dto.RegisterCommand;
import bankingapi.member.exception.NotExistMemberException;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class MemberApplicationServiceTest {

	@Autowired
    MemberApplicationService memberApplicationService;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test
	@DisplayName("이메일, 비밀번호, 이름을 입력받아 회원가입한다.")
	void register() {
		var command = new RegisterCommand(EMAIL, NAME, PASSWORD);

		assertDoesNotThrow(
			() -> memberApplicationService.registerMember(command)
		);
	}

	@Test
	@DisplayName("이메일이 유일하지 않으면 예외가 발생한다.")
	void register_emailIsUnique() {
		var duplicatedEmail = EMAIL;
		memberRepository.save(new Member(duplicatedEmail, NAME, passwordEncoder.encode(PASSWORD)));
		var command = new RegisterCommand(duplicatedEmail, NAME, PASSWORD);

		assertThatThrownBy(
			() -> memberApplicationService.registerMember(command)
		).isInstanceOf(DuplicateEmailException.class);
	}

	@Test
	@DisplayName("저장할 때 비밀번호를 암호화한다.")
	void register_encodePassword() {
		var command = new RegisterCommand(EMAIL, NAME, PASSWORD);
		memberApplicationService.registerMember(command);
		var password = memberRepository.findByEmail(EMAIL).get().getPassword();
		assertThat(passwordEncoder.matches(PASSWORD, password)).isTrue();
	}

	@Test
	@DisplayName("사용자 식별자를 이용해 사용자의 정보를 조회한다.")
	void findMe() {
		memberRepository.save(new Member(EMAIL, NAME, passwordEncoder.encode(PASSWORD)));
		assertDoesNotThrow(
			() -> memberApplicationService.getMember(EMAIL)
		);
	}

	@Test
	@DisplayName("사용자가 없으면 IllegalUserException 예외를 발생한다.")
	void findMe_NotExistMember() {
		var notExistMemberByEmail = EMAIL;

		assertThatThrownBy(
			() -> memberApplicationService.getMember(notExistMemberByEmail)
		).isInstanceOf(NotExistMemberException.class);
	}
}
