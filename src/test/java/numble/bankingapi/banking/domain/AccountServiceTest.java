package numble.bankingapi.banking.domain;

import static numble.bankingapi.fixture.AccountFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import numble.bankingapi.banking.exception.InvalidMemberException;
import numble.bankingapi.banking.exception.NotNegativeMoneyException;
import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberRepository;
import numble.bankingapi.util.AccountNumberGenerator;

@Transactional
@SpringBootTest
class AccountServiceTest {
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private AccountService accountService;
	Account 사용자_계좌;
	Account 상대방_계좌;
	Member 사용자;
	Member 사용자의_친구1;
	Member 사용자의_친구2;

	@BeforeEach
	void setUp() {
		사용자 = memberRepository.findByEmail("member@email.com").get();
		사용자의_친구1 = memberRepository.save(new Member("member_friend1@email.com", "friend1", "password"));
		사용자의_친구2 = memberRepository.save(new Member("member_friend2@email.com", "friend1", "password"));

		사용자_계좌 = accountRepository.save(
			Account.builder()
				.userId(사용자.getId())
				.accountNumber(AccountNumberGenerator.generate())
				.balance(이만원)
				.build()
		);

		상대방_계좌 = accountRepository.save(
			Account.builder()
				.userId(1233L)
				.accountNumber(AccountNumberGenerator.generate())
				.balance(삼만원)
				.build()
		);

		accountRepository.save(
			Account.builder()
				.userId(사용자의_친구1.getId())
				.accountNumber(AccountNumberGenerator.generate())
				.balance(삼만원)
				.build()
		);

		accountRepository.save(
			Account.builder()
				.userId(사용자의_친구2.getId())
				.accountNumber(AccountNumberGenerator.generate())
				.balance(삼만원)
				.build()
		);

		accountRepository.save(
			Account.builder()
				.userId(사용자의_친구2.getId())
				.accountNumber(AccountNumberGenerator.generate())
				.balance(삼만원)
				.build()
		);

		accountRepository.save(
			Account.builder()
				.userId(사용자의_친구2.getId())
				.accountNumber(AccountNumberGenerator.generate())
				.balance(삼만원)
				.build()
		);
	}

	@Test
	@DisplayName("계좌를 개설한다.")
	void save() {
		assertDoesNotThrow(
			() -> accountService.save(3L)
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
		accountService.depositMoney(사용자.getEmail(), 사용자_계좌.getAccountNumber(), 입금할_금액_삼만원);

		// then
		assertThat(사용자_계좌.getBalance()).isEqualTo(계좌_잔액_이만원.plus(입금할_금액_삼만원));
	}

	@Test
	@DisplayName("입금할 때, 해당 사용자가 아니면 예외가 발생한다.")
	void deposit_accessInvalidMember() {
		assertThatThrownBy(
			() -> accountService.depositMoney(사용자.getEmail(), 상대방_계좌.getAccountNumber(), 만원)
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌에 출금하다.")
	void withDrawMoney() {
		Money 계좌_잔액_이만원 = 사용자_계좌.getBalance();
		Money 출금할_금액_만원 = 만원;

		// when
		accountService.withdrawMoney(사용자.getEmail(), 사용자_계좌.getAccountNumber(), 출금할_금액_만원);

		// then
		assertThat(사용자_계좌.getBalance()).isEqualTo(계좌_잔액_이만원.minus(출금할_금액_만원));
	}

	@Test
	@DisplayName("계좌에 들어있는 돈보다 많이 출금하게 되면 NotNegativeMoneyException 예외가 발생한다.")
	void withDrawMoney_() {
		assertThatThrownBy(
			() -> accountService.withdrawMoney(사용자.getEmail(), 사용자_계좌.getAccountNumber(), 삼만원)
		).isInstanceOf(NotNegativeMoneyException.class);
	}

	@Test
	@DisplayName("출금할 때, 해당 사용자가 아니면 예외가 발생한다.")
	void withdraw_accessInvalidMember() {
		assertThatThrownBy(
			() -> accountService.withdrawMoney(사용자.getEmail(), 상대방_계좌.getAccountNumber(), 만원)
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌 이체한다.")
	void transferMoney() {
		Money 이전_사용자_잔액_이만원 = 사용자_계좌.getBalance();
		Money 이전_상대방_잔액_만원 = 상대방_계좌.getBalance();
		Money 이체할_금액_만원 = 만원;

		// when
		accountService.transferMoney(사용자.getEmail(), 사용자_계좌.getAccountNumber(), 상대방_계좌.getAccountNumber(), 이체할_금액_만원);

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
				() -> accountService.transferMoney(사용자.getEmail(), 사용자_계좌.getAccountNumber(), 상대방_계좌.getAccountNumber(),
					이체할_금액_삼만원)
			).isInstanceOf(NotNegativeMoneyException.class),
			() -> assertThat(이전_사용자_잔액_이만원).isEqualTo(이만원),
			() -> assertThat(이전_상대방_잔액_만원).isEqualTo(삼만원)
		);
	}

	@Test
	@DisplayName("사용자는 같은 계좌에 이체할 수 없다.")
	void transferMoney_notTransferSameAccount() {
		assertThatThrownBy(
			() -> accountService.transferMoney(사용자.getEmail(), 사용자_계좌.getAccountNumber(), 사용자_계좌.getAccountNumber(),
				삼만원)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("이체할 때, 해당 사용자가 아니면 예외가 발생한다.")
	void transfer_accessInvalidMember() {
		assertThatThrownBy(
			() -> accountService.transferMoney(사용자.getEmail(), 상대방_계좌.getAccountNumber(), 사용자_계좌.getAccountNumber(),
				삼만원)
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌 번호로 계좌를 조회할 수 있다.")
	void getAccountByAccountNumber() {
		assertDoesNotThrow(
			() -> accountService.getAccountByAccountNumber(사용자_계좌.getAccountNumber())
		);
	}

	@Test
	@DisplayName("계좌 사용기록을 반환할 때, 해당 사용자가 아니면 예외가 발생한다.")
	void getHistory_accessInvalidMember() {
		assertThatThrownBy(
			() -> accountService.findAccountHistoriesByFromAccountNumber(사용자.getEmail(), 상대방_계좌.getAccountNumber())
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("모든 계좌를 조회한다.")
	void findAll() {
		int size = accountRepository.findAll().size();
		List<Account> accounts = accountService.findAll();
		assertThat(accounts).hasSize(size);
	}

	@Test
	@DisplayName("친구들의 계좌를 조회한다.")
	void getFriendAccounts() {
		var accounts = assertDoesNotThrow(
			() -> accountService.getFriendAccounts(List.of(사용자의_친구1.getId(), 사용자의_친구2.getId()))
		);

		assertThat(accounts).hasSize(4);
	}
}
