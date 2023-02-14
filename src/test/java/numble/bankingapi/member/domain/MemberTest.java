package numble.bankingapi.member.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MemberTest {

	@Test
	@DisplayName("사용자는 사용자 식별자(userId), 아이디(Id), 이름(name), 비밀번호(Password)를 가진다.")
	void createdMember() {
		String email = "rjsckdd12@gmail.com";
		String name = "this-is-spear";
		String password = "password";

		assertDoesNotThrow(
			() -> Member.builder()
				.email(email)
				.name(name)
				.password(password)
				.build()
		);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("id 가 Null 이면 NullPointerException 이 발생한다.")
	void createMember_idNotNull(String email) {
		String name = "this-is-spear";
		String password = "password";

		assertThatThrownBy(
			() -> Member.builder()
				.email(email)
				.name(name)
				.password(password)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("name 이 Null 이면 NullPointerException 이 발생한다.")
	void createMember_nameNotNull(String name) {
		String email = "rjsckdd12@gmail.com";
		String password = "password";

		assertThatThrownBy(
			() -> Member.builder()
				.email(email)
				.name(name)
				.password(password)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("password 가 Null 이면 NullPointerException 이 발생한다.")
	void createMember_NotNull(String password) {
		String email = "rjsckdd12@gmail.com";
		String name = "this-is-spear";

		assertThatThrownBy(
			() -> Member.builder()
				.email(email)
				.name(name)
				.password(password)
				.build()
		).isInstanceOf(NullPointerException.class);
	}
}
