package numble.bankingapi.banking.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AccountNumberTest {

	@ParameterizedTest
	@DisplayName("계좌 번호를 생성할 수 있다.")
	@ValueSource(strings = {"123-2342-2342", "1230-234-123"})
	void createAccountNumber(String 문자열) {
		assertDoesNotThrow(
			() -> new AccountNumber(문자열)
		);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("계좌 번호가 비어있으면 예외가 발생합니다.")
	void createAccountNumber_NotEmptyAndNull(String 유효하지_않은_문자열) {
		assertThatThrownBy(
			() -> new AccountNumber(유효하지_않은_문자열)
		).isInstanceOf(InvalidAccountNumberException.class);
	}

	@ParameterizedTest
	@DisplayName("계좌 번호를 새성할 수 있다.")
	@ValueSource(strings = {"qwe-213-df", "**-&&213-123"})
	void createAccountNumber_OnlyNumberAndBar(String 유효하지_않은_문자열) {
		assertThatThrownBy(
			() -> new AccountNumber(유효하지_않은_문자열)
		).isInstanceOf(InvalidAccountNumberException.class);
	}
}
