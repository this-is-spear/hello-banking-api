package numble.bankingapi.banking.domain;

import static numble.bankingapi.fixture.AccountFixture.*;
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
				.fromAccountNumber(계좌번호)
				.toAccountNumber(상대방_계좌번호)
				.money(이만원)
				.type(입금)
				.build()
		);
	}

	@Test
	@DisplayName("fromAccountNumber 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_fromAccountNumberNotNull() {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(null)
				.toAccountNumber(상대방_계좌번호)
				.money(이만원)
				.type(입금)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("toAccountNumber 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_toAccountNumberNotNull() {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(계좌번호)
				.toAccountNumber(null)
				.money(이만원)
				.type(입금)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("money 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_moneyNotNull() {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(계좌번호)
				.toAccountNumber(상대방_계좌번호)
				.money(null)
				.type(입금)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("type 은 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_typeNotNull() {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(계좌번호)
				.toAccountNumber(상대방_계좌번호)
				.money(이만원)
				.build()
		).isInstanceOf(NullPointerException.class);
	}
}
