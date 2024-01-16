package bankingapi.banking.domain;

import static bankingapi.fixture.AccountFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

class AccountHistoryTest {

	@EnumSource
	@ParameterizedTest
	@DisplayName("히스토리 식별자(Id), 계좌 번호(FromAccountNumber)와, 상대 계좌 번호(ToAccountNumber) 기록 타입(HistoryType)과 금액(Money), 기록 날짜(RecordDate)을 포함한다.")
	void createAccountHistory(HistoryType 타입) {
		assertDoesNotThrow(
			() -> AccountHistory.builder()
				.fromAccountNumber(계좌번호)
				.toAccountNumber(상대방_계좌번호)
				.money(이만원)
				.balance(이만원)
				.type(타입)
				.build()
		);
	}

	@NullSource
	@ParameterizedTest
	@DisplayName("fromAccountNumber 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_fromAccountNumberNotNull(AccountNumber 계좌번호) {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(계좌번호)
				.toAccountNumber(상대방_계좌번호)
				.money(이만원)
				.balance(이만원)
				.type(입금)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@NullSource
	@ParameterizedTest
	@DisplayName("toAccountNumber 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_toAccountNumberNotNull(AccountNumber 상대방_계좌번호) {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(계좌번호)
				.toAccountNumber(상대방_계좌번호)
				.money(이만원)
				.balance(이만원)
				.type(입금)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@NullSource
	@ParameterizedTest
	@DisplayName("money 는 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_moneyNotNull(Money 비어있는_금액) {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(계좌번호)
				.toAccountNumber(상대방_계좌번호)
				.money(비어있는_금액)
				.balance(이만원)
				.type(입금)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@NullSource
	@ParameterizedTest
	@DisplayName("type 은 Null 이면 NullPointerException 예외가 발생한다.")
	void createAccountHistory_typeNotNull(HistoryType 비어있는_타입) {
		assertThatThrownBy(
			() -> AccountHistory.builder()
				.fromAccountNumber(계좌번호)
				.toAccountNumber(상대방_계좌번호)
				.money(이만원)
				.balance(이만원)
				.type(비어있는_타입)
				.build()
		).isInstanceOf(NullPointerException.class);
	}
}
