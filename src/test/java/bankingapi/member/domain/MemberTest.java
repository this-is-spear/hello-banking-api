package bankingapi.member.domain;

import static bankingapi.fixture.MemberFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import bankingapi.member.exception.InvalidFormatException;

class MemberTest {

	@Test
	@DisplayName("사용자는 사용자 식별자(userId), 아이디(Id), 이름(name), 비밀번호(Password)를 가진다.")
	void createdMember() {
		assertDoesNotThrow(
			() -> Member.builder()
				.email(EMAIL)
				.name(NAME)
				.password(PASSWORD)
				.build()
		);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("id 가 Null 이면 NullPointerException 이 발생한다.")
	void createMember_idNotNull(String email) {
		assertThatThrownBy(
			() -> Member.builder()
				.email(email)
				.name(NAME)
				.password(PASSWORD)
				.build()
		).isInstanceOf(InvalidFormatException.class);
	}

	@Test
	@DisplayName("이메일은 이메일 규격에 맞아야 한다.")
	void createMember_emailRequiredFormat() {
		String invalidEmail = "invalid email format";

		assertThatThrownBy(
			() -> Member.builder()
				.email(invalidEmail)
				.name(NAME)
				.password(PASSWORD)
				.build()
		).isInstanceOf(InvalidFormatException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("name 이 Null 이면 NullPointerException 이 발생한다.")
	void createMember_nameNotNull(String invalidName) {
		assertThatThrownBy(
			() -> Member.builder()
				.email(EMAIL)
				.name(invalidName)
				.password(PASSWORD)
				.build()
		).isInstanceOf(InvalidFormatException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("password 가 Null 이면 NullPointerException 이 발생한다.")
	void createMember_NotNull(String invalidPassword) {
		assertThatThrownBy(
			() -> Member.builder()
				.email(EMAIL)
				.name(NAME)
				.password(invalidPassword)
				.build()
		).isInstanceOf(InvalidFormatException.class);
	}
}
