package numble.bankingapi.banking.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountHistoryTest {
	@Test
	@DisplayName("히스토리 식별자(Id), 계좌 번호(FromAccountNumber)와, 상대 계좌 번호(ToAccountNumber) 기록 타입(HistoryType)과 금액(Money), 기록 날짜(RecordDate)을 포함한다.")
	void createAccountHistory() {
		assertDoesNotThrow(
			() -> AccountHistory.builder()
				.fromAccountNumber(new AccountNumber("123-324-34"))
				.toAccountNumber(new AccountNumber("34-4353-1322"))
				.money(new Money(20_000L))
				.type(HistoryType.DEPOSIT)
				.build()
		);
	}

	@Test
	@DisplayName("fromAccountNumber 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_fromAccountNumberNotNull() {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.toAccountNumber(new AccountNumber("34-4353-1322"))
				.money(new Money(20_000L))
				.type(HistoryType.DEPOSIT)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("toAccountNumber 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_toAccountNumberNotNull() {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(new AccountNumber("123-324-34"))
				.money(new Money(20_000L))
				.type(HistoryType.DEPOSIT)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("money 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_moneyNotNull() {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(new AccountNumber("123-324-34"))
				.toAccountNumber(new AccountNumber("34-4353-1322"))
				.type(HistoryType.DEPOSIT)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("type 은 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_typeNotNull() {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(new AccountNumber("123-324-34"))
				.toAccountNumber(new AccountNumber("34-4353-1322"))
				.money(new Money(20_000L))
				.build()
		).isInstanceOf(NullPointerException.class);
	}
}
