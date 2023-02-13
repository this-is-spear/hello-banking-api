package numble.bankingapi.banking.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {

	@ParameterizedTest
	@DisplayName("금액은 동의 양을 포함한다.")
	@ValueSource(longs = {10_000L, 20_000L, 30_000L, 13_000L})
	void createMoney(Long 양수) {
		assertDoesNotThrow(() -> new Money(양수));
	}

	@ParameterizedTest
	@DisplayName("금액이 음수가 되면 예외가 발생한다.")
	@ValueSource(longs = {-10_000L, -20_000L, -30_000L, -1L})
	void createMoney_canNotBeNegative(Long 음수) {
		assertThatThrownBy(
			() -> new Money(음수)
		).isInstanceOf(NotNegativeException.class);
	}
}
