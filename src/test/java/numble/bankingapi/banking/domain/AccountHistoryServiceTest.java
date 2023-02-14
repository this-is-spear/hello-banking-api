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
	AccountHistoryService accountHistoryService;

	@Test
	@DisplayName("입금할 때 기록한다.")
	void deposit() {
		AccountNumber accountNumber = AccountNumberGenerator.generate();
		assertDoesNotThrow(
			() -> accountHistoryService.recordCompletionDepositMoney(accountNumber, 이만원)
		);
		List<AccountHistory> accountHistories = accountHistoryRepository.findByFromAccountNumber(accountNumber);
		AccountHistory accountHistory = accountHistories.get(accountHistories.size() - 1);
		assertAll(
			() -> assertThat(accountHistory.getType()).isEqualTo(HistoryType.DEPOSIT),
			() -> assertThat(accountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(accountHistory.getToAccountNumber()).isEqualTo(accountNumber)
		);
	}

	@Test
	@DisplayName("출금할 때 기록한다.")
	void withdraw() {
		AccountNumber accountNumber = AccountNumberGenerator.generate();
		assertDoesNotThrow(
			() -> accountHistoryService.recordCompletionWithdrawMoney(accountNumber, 이만원)
		);
		List<AccountHistory> accountHistories = accountHistoryRepository.findByFromAccountNumber(accountNumber);
		AccountHistory accountHistory = accountHistories.get(accountHistories.size() - 1);
		assertAll(
			() -> assertThat(accountHistory.getType()).isEqualTo(HistoryType.WITHDRAW),
			() -> assertThat(accountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(accountHistory.getToAccountNumber()).isEqualTo(accountNumber)
		);
	}

	@Test
	@DisplayName("이체할 때 기록한다.")
	void transfer() {
		AccountNumber fromAccountNumber = AccountNumberGenerator.generate();
		AccountNumber toAccountNumber = AccountNumberGenerator.generate();

		assertDoesNotThrow(
			() -> accountHistoryService.recordCompletionTransferMoney(fromAccountNumber, toAccountNumber, 이만원)
		);

		List<AccountHistory> fromAccountHistories = accountHistoryRepository.findByFromAccountNumber(fromAccountNumber);
		AccountHistory fromAccountHistory = fromAccountHistories.get(fromAccountHistories.size() - 1);

		List<AccountHistory> toAccountHistories = accountHistoryRepository.findByFromAccountNumber(toAccountNumber);
		AccountHistory toAccountHistory = toAccountHistories.get(toAccountHistories.size() - 1);

		assertAll(
			() -> assertThat(fromAccountHistory.getType()).isEqualTo(HistoryType.WITHDRAW),
			() -> assertThat(fromAccountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(fromAccountHistory.getFromAccountNumber()).isEqualTo(fromAccountNumber),
			() -> assertThat(fromAccountHistory.getToAccountNumber()).isEqualTo(toAccountNumber),
			() -> assertThat(toAccountHistory.getType()).isEqualTo(HistoryType.DEPOSIT),
			() -> assertThat(toAccountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(toAccountHistory.getFromAccountNumber()).isEqualTo(toAccountNumber),
			() -> assertThat(toAccountHistory.getToAccountNumber()).isEqualTo(fromAccountNumber)
		);
	}
}
