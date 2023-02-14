package numble.bankingapi.banking.domain;

import static numble.bankingapi.fixture.AccountFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class AccountServiceTest {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountService accountService;
	Account 사용자_계좌;
	Account 상대방_계좌;

	@BeforeEach
	void setUp() {
		사용자_계좌 = accountRepository.save(
			Account.builder()
				.userId(2L)
				.accountNumber(계좌번호)
				.balance(이만원)
				.build()
		);

		상대방_계좌 = accountRepository.save(
			Account.builder()
				.userId(1L)
				.accountNumber(상대방_계좌번호)
				.balance(삼만원)
				.build()
		);
	}

	@Test
	@DisplayName("계좌를 조회한다.")
	void findById() {
		// when
		Account 찾은_계좌 = accountService.findById(사용자_계좌.getId());

		// then
		assertThat(사용자_계좌).isEqualTo(찾은_계좌);
	}

	@Test
	@DisplayName("계좌에 입금하다.")
	void depositMoney() {
		Money 계좌_잔액_이만원 = 사용자_계좌.getBalance();
		Money 입금할_금액_삼만원 = 삼만원;

		// when
		accountService.depositMoney(사용자_계좌.getAccountNumber(), 입금할_금액_삼만원);

		// then
		assertThat(사용자_계좌.getBalance()).isEqualTo(계좌_잔액_이만원.plus(입금할_금액_삼만원));
	}

	@Test
	@DisplayName("계좌에 출금하다.")
	void withDrawMoney() {
		Money 계좌_잔액_이만원 = 사용자_계좌.getBalance();
		Money 출금할_금액_만원 = 만원;

		// when
		accountService.withdrawMoney(사용자_계좌.getAccountNumber(), 출금할_금액_만원);

		// then
		assertThat(사용자_계좌.getBalance()).isEqualTo(계좌_잔액_이만원.minus(출금할_금액_만원));
	}

	@Test
	@DisplayName("계좌에 들어있는 돈보마 많이 출금하게 되면 NotNegativeMoneyException 예외가 발생한다.")
	void withDrawMoney_() {
		Money 출금할_금액_삼만원 = 삼만원;

		assertThatThrownBy(
			() -> accountService.withdrawMoney(사용자_계좌.getAccountNumber(), 출금할_금액_삼만원)
		).isInstanceOf(NotNegativeMoneyException.class);
	}

	@Test
	@DisplayName("계좌 이체한다.")
	void transferMoney() {
		Money 이전_사용자_잔액_이만원 = 사용자_계좌.getBalance();
		Money 이전_상대방_잔액_만원 = 상대방_계좌.getBalance();
		Money 이체할_금액_만원 = 만원;

		// when
		accountService.transferMoney(사용자_계좌.getAccountNumber(), 상대방_계좌.getAccountNumber(), 이체할_금액_만원);

		// then
		assertAll(
			() -> assertThat(사용자_계좌.getBalance()).isEqualTo(이전_사용자_잔액_이만원.minus(이체할_금액_만원)),
			() -> assertThat(상대방_계좌.getBalance()).isEqualTo(이전_상대방_잔액_만원.plus(이체할_금액_만원))
		);
	}

	@Test
	@DisplayName("계좌 이체에 실패하면 모든 정보가 복구된다.")
	void transferMoney_ifFailedRollback() {
		Money 이전_사용자_잔액_이만원 = 사용자_계좌.getBalance();
		Money 이전_상대방_잔액_만원 = 상대방_계좌.getBalance();
		Money 이체할_금액_삼만원 = 삼만원;

		assertAll(
			() -> assertThatThrownBy(
				() -> accountService.transferMoney(사용자_계좌.getAccountNumber(), 상대방_계좌.getAccountNumber(), 이체할_금액_삼만원)
			).isInstanceOf(NotNegativeMoneyException.class),
			() -> assertThat(이전_사용자_잔액_이만원).isEqualTo(이만원),
			() -> assertThat(이전_상대방_잔액_만원).isEqualTo(삼만원)
		);
	}
}
