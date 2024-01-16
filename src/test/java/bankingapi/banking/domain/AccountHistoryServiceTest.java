package bankingapi.banking.domain;

import static bankingapi.fixture.AccountFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import bankingapi.fake.FakeAccountHistoryRepository;
import bankingapi.fake.FakeAccountRepository;
import bankingapi.fake.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bankingapi.member.domain.Member;
import bankingapi.util.generator.AccountNumberGenerator;

class AccountHistoryServiceTest {
	FakeAccountRepository accountRepository = new FakeAccountRepository();
	FakeAccountHistoryRepository accountHistoryRepository = new FakeAccountHistoryRepository();
	FakeMemberRepository memberRepository = new FakeMemberRepository();
	AccountService accountService;
	Member 사용자;
	Account 사용자_계좌;

	@BeforeEach
	void setUp() {
		accountService = new AccountService(accountRepository, accountHistoryRepository);

		사용자 = memberRepository.save(new Member("member@email.com", "friend1", "password"));
		사용자_계좌 = accountRepository.save(
			Account.builder()
				.userId(사용자.getId())
				.accountNumber(계좌번호)
				.balance(이만원)
				.build()
		);
	}

	@Test
	@DisplayName("입금할 때 기록한다.")
	void deposit() {
		accountService.depositMoney(사용자_계좌.getAccountNumber(), 이만원);

		var accountHistories = accountHistoryRepository.findByFromAccountNumber(
			사용자_계좌.getAccountNumber());

		assertThat(accountHistories).hasSize(1);
		AccountHistory accountHistory = accountHistories.get(accountHistories.size() - 1);

		assertAll(
			() -> assertThat(accountHistory.getType()).isEqualTo(HistoryType.DEPOSIT),
			() -> assertThat(accountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(accountHistory.getToAccountNumber()).isEqualTo(사용자_계좌.getAccountNumber())
			// () -> assertThat(accountHistory.getBalance()).isEqualTo(이만원.plus(이만원))
		);
	}

	@Test
	@DisplayName("출금할 때 기록한다.")
	void withdraw() {
		accountService.withdrawMoney(사용자_계좌.getAccountNumber(), 이만원);

		var accountHistories = accountHistoryRepository.findByFromAccountNumber(
			사용자_계좌.getAccountNumber());

		assertThat(accountHistories).hasSize(1);
		AccountHistory accountHistory = accountHistories.get(accountHistories.size() - 1);

		assertAll(
			() -> assertThat(accountHistory.getType()).isEqualTo(HistoryType.WITHDRAW),
			() -> assertThat(accountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(accountHistory.getToAccountNumber()).isEqualTo(사용자_계좌.getAccountNumber()),
			() -> assertThat(accountHistory.getBalance()).isEqualTo(이만원.minus(이만원))
		);
	}

	@Test
	@DisplayName("이체할 때 기록한다.")
	void transfer() {
		var fromAccount = Account.builder()
			.userId(사용자.getId())
			.balance(이만원)
			.accountNumber(AccountNumberGenerator.generate())
			.build();

		var toAccount = Account.builder()
			.userId(999L)
			.balance(이만원)
			.accountNumber(AccountNumberGenerator.generate())
			.build();

		var 사용자_계좌번호 = accountRepository.save(fromAccount).getAccountNumber();
		var 상대방_계좌번호 = accountRepository.save(toAccount).getAccountNumber();

		assertDoesNotThrow(
			() -> accountService.transferMoney(사용자_계좌번호, 상대방_계좌번호, 이만원));

		var fromAccountHistories = accountHistoryRepository
			.findByFromAccountNumber(사용자_계좌번호);
		AccountHistory fromAccountHistory = fromAccountHistories.get(fromAccountHistories.size() - 1);

		var toAccountHistories = accountHistoryRepository.findByFromAccountNumber(상대방_계좌번호);
		var toAccountHistory = toAccountHistories.get(toAccountHistories.size() - 1);

		assertAll(
			() -> assertThat(fromAccountHistory.getType()).isEqualTo(HistoryType.WITHDRAW),
			() -> assertThat(fromAccountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(fromAccountHistory.getBalance()).isEqualTo(이만원.minus(이만원)),
			() -> assertThat(fromAccountHistory.getFromAccountNumber()).isEqualTo(사용자_계좌번호),
			() -> assertThat(fromAccountHistory.getToAccountNumber()).isEqualTo(상대방_계좌번호),
			() -> assertThat(toAccountHistory.getType()).isEqualTo(HistoryType.DEPOSIT),
			() -> assertThat(toAccountHistory.getMoney()).isEqualTo(이만원),
			() -> assertThat(toAccountHistory.getBalance()).isEqualTo(이만원.plus(이만원)),
			() -> assertThat(toAccountHistory.getFromAccountNumber()).isEqualTo(상대방_계좌번호),
			() -> assertThat(toAccountHistory.getToAccountNumber()).isEqualTo(사용자_계좌번호)
		);
	}

	@Test
	@DisplayName("계좌 번호로 기록을 찾는다.")
	void findByFromAccountNumber() {
		accountHistoryRepository.save(AccountHistory.builder()
			.fromAccountNumber(사용자_계좌.getAccountNumber())
			.toAccountNumber(상대방_계좌번호)
			.money(만원)
			.balance(만원)
			.type(HistoryType.DEPOSIT)
			.build());

		accountHistoryRepository.save(AccountHistory.builder()
			.fromAccountNumber(사용자_계좌.getAccountNumber())
			.toAccountNumber(상대방_계좌번호)
			.money(만원)
			.balance(만원)
			.type(HistoryType.DEPOSIT)
			.build());
		var histories = accountService.findAccountHistoriesByFromAccountNumber(사용자_계좌);
		assertThat(histories).hasSize(2);
	}
}
