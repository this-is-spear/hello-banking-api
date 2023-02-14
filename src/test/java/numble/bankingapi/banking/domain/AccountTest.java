package numble.bankingapi.banking.domain;

import static numble.bankingapi.fixture.AccountFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountTest {

	@Test
	@DisplayName("계좌를 생성한다.")
	void createAccount() {
		assertDoesNotThrow(
			() -> Account.builder()
				.accountNumber(계좌번호)
				.balance(이만원)
				.userId(사용자_ID)
				.build()
		);
	}

	@Test
	@DisplayName("accountNumber 가 Null 이면 NullPoint 가 발생한다.")
	void createAccount_accountNumberNotNull() {
		assertThatThrownBy(
			() -> Account.builder()
				.accountNumber(null)
				.balance(이만원)
				.userId(사용자_ID)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("balance 가 Null 이면 NullPoint 가 발생한다.")
	void createAccount_balanceNotNull() {
		assertThatThrownBy(
			() -> Account.builder()
				.accountNumber(계좌번호)
				.balance(null)
				.userId(사용자_ID)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("userId 가 Null 이면 NullPoint 가 발생한다.")
	void createAccount_userIdNotNull() {
		assertThatThrownBy(
			() -> Account.builder()
				.accountNumber(계좌번호)
				.balance(이만원)
				.userId(null)
				.build()
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("계좌에 돈을 입금할 수 있다.")
	void deposit() {
		var 계좌 = 계좌_잔액_이만원();

		assertDoesNotThrow(
			() -> 계좌.deposit(만원)
		);

		assertThat(계좌.getBalance()).isEqualTo(삼만원);
	}

	@Test
	@DisplayName("계좌에 돈을 출금할 수 있다.")
	void withdraw() {
		var 계좌 = 계좌_잔액_이만원();

		assertDoesNotThrow(
			() -> 계좌.withdraw(만원)
		);

		assertThat(계좌.getBalance()).isEqualTo(만원);
	}

	@Test
	@DisplayName("계좌 잔액보다 많은 돈을 출금하면 실패한다.")
	void withdraw_notOver() {
		var 계좌 = 계좌_잔액_이만원();

		assertThatThrownBy(
			() -> 계좌.withdraw(삼만원)
		).isInstanceOf(NotNegativeMoneyException.class);
	}

	private Account 계좌_잔액_이만원() {
		return Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(사용자_ID)
			.build();
	}
}
