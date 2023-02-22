package numble.bankingapi.banking.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.banking.exception.InvalidMemberException;
import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberRepository;
import numble.bankingapi.util.AccountNumberGenerator;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final AccountHistoryRepository accountHistoryRepository;
	private final MemberRepository memberRepository;

	public Account save(Long userId) {
		AccountNumber accountNumber;

		do {
			accountNumber = AccountNumberGenerator.generate();
		} while (accountRepository.findByAccountNumber(accountNumber).isPresent());

		return accountRepository.save(
			Account.builder()
				.accountNumber(accountNumber)
				.balance(Money.zero())
				.userId(userId)
				.build()
		);
	}

	public Account findById(Long id) {
		return accountRepository.findById(id).orElseThrow();
	}

	public Account getAccountByAccountNumber(AccountNumber accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber).orElseThrow();
	}

	@Transactional
	public void depositMoney(String principal, AccountNumber accountNumber, Money money) {
		Account account = getAccountByAccountNumber(accountNumber);
		validateMember(principal, account);
		account.deposit(money);
		recordCompletionDepositMoney(account, money);
	}

	@Transactional
	public void withdrawMoney(AccountNumber accountNumber, Money money) {
		Account account = getAccountByAccountNumber(accountNumber);
		account.withdraw(money);
		recordCompletionWithdrawMoney(account, money);
	}

	@Transactional
	public void transferMoney(String principal, AccountNumber fromAccountNumber, AccountNumber toAccountNumber,
		Money money) {
		Account fromAccount = getAccountByAccountNumber(fromAccountNumber);
		Account toAccount = getAccountByAccountNumber(toAccountNumber);
		validateMember(principal, fromAccount);

		if (fromAccount.equals(toAccount)) {
			throw new IllegalArgumentException();
		}

		fromAccount.withdraw(money);
		toAccount.deposit(money);
		recordCompletionTransferMoney(fromAccount, toAccount, money);
	}

	private void validateMember(String principal, Account account) {
		Member member = memberRepository.findByEmail(principal).orElseThrow(InvalidMemberException::new);

		if (!member.getId().equals(account.getUserId())) {
			throw new InvalidMemberException();
		}
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public List<AccountHistory> findAccountHistoriesByFromAccountNumber(String principal, AccountNumber accountNumber) {
		Account account = getAccountByAccountNumber(accountNumber);
		validateMember(principal, account);
		return accountHistoryRepository.findByFromAccountNumber(accountNumber);
	}

	private void recordCompletionDepositMoney(Account fromAccount, Money money) {
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(fromAccount.getAccountNumber())
				.toAccountNumber(fromAccount.getAccountNumber())
				.type(HistoryType.DEPOSIT)
				.money(money)
				.balance(fromAccount.getBalance())
				.build());
	}

	private void recordCompletionWithdrawMoney(Account fromAccount, Money money) {
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(fromAccount.getAccountNumber())
				.toAccountNumber(fromAccount.getAccountNumber())
				.type(HistoryType.WITHDRAW)
				.money(money)
				.balance(fromAccount.getBalance())
				.build());
	}

	private void recordCompletionTransferMoney(Account fromAccount, Account toAccount, Money money) {
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(fromAccount.getAccountNumber())
				.toAccountNumber(toAccount.getAccountNumber())
				.type(HistoryType.WITHDRAW)
				.money(money)
				.balance(fromAccount.getBalance())
				.build());
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(toAccount.getAccountNumber())
				.toAccountNumber(fromAccount.getAccountNumber())
				.type(HistoryType.DEPOSIT)
				.money(money)
				.balance(toAccount.getBalance())
				.build());
	}
}
