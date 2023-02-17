package numble.bankingapi.banking.domain;

import static numble.bankingapi.fixture.AccountFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import numble.bankingapi.util.AccountNumberGenerator;

@Transactional
@SpringBootTest
class AccountHistoryServiceTest {

	@Autowired
	AccountHistoryRepository accountHistoryRepository;

	@Autowired
	AccountService accountService;

	@Test
	@DisplayName("입금할 때 기록한다.")
	void deposit() {
		Account account = Account.builder()
			.userId(2L)
			.balance(이만원)
			.accountNumber(AccountNumberGenerator.generate())
			.build();

		assertDoesNotThrow(() -> accountService.depositMoney(account, 이만원));

		List<AccountHistory> accountHistories = accountHistoryRepository.findByFromAccountNumber(
			account.getAccountNumber());
		AccountHistory accountHistory = accountHistories.get(accountHistories.size() - 1);

		assertAll(
			() -> assertThat(accountHistory.getType()).isEqualTo(HistoryType.DEPOSIT),
			() -> assertThat(accountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(accountHistory.getToAccountNumber()).isEqualTo(account.getAccountNumber()),
			() -> assertThat(accountHistory.getBalance()).isEqualTo(이만원.plus(이만원))
		);
	}

	@Test
	@DisplayName("출금할 때 기록한다.")
	void withdraw() {
		Account account = Account.builder()
			.userId(2L)
			.balance(이만원)
			.accountNumber(AccountNumberGenerator.generate())
			.build();

		assertDoesNotThrow(() -> accountService.withdrawMoney(account, 이만원));

		List<AccountHistory> accountHistories = accountHistoryRepository.findByFromAccountNumber(
			account.getAccountNumber());
		AccountHistory accountHistory = accountHistories.get(accountHistories.size() - 1);

		assertAll(
			() -> assertThat(accountHistory.getType()).isEqualTo(HistoryType.WITHDRAW),
			() -> assertThat(accountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(accountHistory.getToAccountNumber()).isEqualTo(account.getAccountNumber()),
			() -> assertThat(accountHistory.getBalance()).isEqualTo(이만원.minus(이만원))
		);
	}

	@Test
	@DisplayName("이체할 때 기록한다.")
	void transfer() {
		Account fromAccount = Account.builder()
			.userId(2L)
			.balance(이만원)
			.accountNumber(AccountNumberGenerator.generate())
			.build();

		Account toAccount = Account.builder()
			.userId(2L)
			.balance(이만원)
			.accountNumber(AccountNumberGenerator.generate())
			.build();

		assertDoesNotThrow(() -> accountService.transferMoney(fromAccount, toAccount, 이만원));

		AccountNumber fromAccountAccountNumber = fromAccount.getAccountNumber();
		AccountNumber toAccountAccountNumber = toAccount.getAccountNumber();

		List<AccountHistory> fromAccountHistories = accountHistoryRepository
			.findByFromAccountNumber(fromAccountAccountNumber);
		AccountHistory fromAccountHistory = fromAccountHistories.get(fromAccountHistories.size() - 1);

		List<AccountHistory> toAccountHistories = accountHistoryRepository
			.findByFromAccountNumber(toAccountAccountNumber);
		AccountHistory toAccountHistory = toAccountHistories.get(toAccountHistories.size() - 1);

		assertAll(
			() -> assertThat(fromAccountHistory.getType()).isEqualTo(HistoryType.WITHDRAW),
			() -> assertThat(fromAccountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(fromAccountHistory.getBalance()).isEqualTo(이만원.minus(이만원)),
			() -> assertThat(fromAccountHistory.getFromAccountNumber()).isEqualTo(fromAccountAccountNumber),
			() -> assertThat(fromAccountHistory.getToAccountNumber()).isEqualTo(toAccountAccountNumber),
			() -> assertThat(toAccountHistory.getType()).isEqualTo(HistoryType.DEPOSIT),
			() -> assertThat(toAccountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(toAccountHistory.getBalance()).isEqualTo(이만원.plus(이만원)),
			() -> assertThat(toAccountHistory.getFromAccountNumber()).isEqualTo(toAccountAccountNumber),
			() -> assertThat(toAccountHistory.getToAccountNumber()).isEqualTo(fromAccountAccountNumber)
		);
	}

	@Test
	@DisplayName("계좌 번호로 기록을 찾는다.")
	void findByFromAccountNumber() {
		accountHistoryRepository.save(AccountHistory.builder()
			.fromAccountNumber(계좌번호)
			.toAccountNumber(상대방_계좌번호)
			.money(만원)
			.balance(만원)
			.type(HistoryType.DEPOSIT)
			.build());

		accountHistoryRepository.save(AccountHistory.builder()
			.fromAccountNumber(계좌번호)
			.toAccountNumber(상대방_계좌번호)
			.money(만원)
			.balance(만원)
			.type(HistoryType.DEPOSIT)
			.build());
		List<AccountHistory> histories = accountService.findAccountHistoriesByFromAccountNumber(계좌번호);
		assertThat(histories).hasSize(2);
	}
}