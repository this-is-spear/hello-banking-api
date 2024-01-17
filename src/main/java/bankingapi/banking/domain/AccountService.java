package bankingapi.banking.domain;

import bankingapi.util.generator.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final AccountHistoryRepository accountHistoryRepository;

	@Transactional
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

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void depositMoney(AccountNumber accountNumber, Money money) {
		var account = getAccountByAccountNumberWithOptimisticLock(accountNumber);
		account.deposit(money);
		recordCompletionDepositMoney(account, money);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void withdrawMoney(AccountNumber accountNumber, Money money) {
		var account = getAccountByAccountNumberWithOptimisticLock(accountNumber);
		account.withdraw(money);
		recordCompletionWithdrawMoney(account, money);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void transferMoney(AccountNumber fromAccountNumber, AccountNumber toAccountNumber,
		Money money) {
		var fromAccount = getAccountByAccountNumberWithOptimisticLock(fromAccountNumber);
		var toAccount = getAccountByAccountNumberWithOptimisticLock(toAccountNumber);

		if (fromAccount.equals(toAccount)) {
			throw new IllegalArgumentException();
		}

		fromAccount.withdraw(money);
		toAccount.deposit(money);

		recordCompletionTransferMoney(fromAccount, toAccount, money);
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public List<Account> getFriendAccounts(List<Long> friendIds) {
		return accountRepository.findAllByUserIdIn(friendIds)
			.stream()
			.toList();
	}

	public List<AccountHistory> findAccountHistoriesByFromAccountNumber(Account account) {
		return accountHistoryRepository.findByFromAccountNumber(account.getAccountNumber());
	}


	public List<Account> getAccountByMemberId(Long memberId) {
		return accountRepository.findByUserId(memberId);
	}

	public Account createAccount(Long id) {
		AccountNumber accountNumber;

		do {
			accountNumber = AccountNumberGenerator.generate();
		} while (accountRepository.findByAccountNumber(accountNumber).isPresent());

		return accountRepository.save(
				Account.builder()
						.accountNumber(accountNumber)
						.balance(Money.zero())
						.userId(id)
						.build()
		);
	}

	private Account getAccountByAccountNumberWithOptimisticLock(AccountNumber accountNumber) {
		return accountRepository.findByAccountNumberWithOptimisticLock(accountNumber).orElseThrow();
	}

	private void recordCompletionDepositMoney(Account fromAccount, Money money) {
		accountHistoryRepository.save(AccountHistory.recordDepositHistory(fromAccount, fromAccount, money));
	}

	private void recordCompletionWithdrawMoney(Account fromAccount, Money money) {
		accountHistoryRepository.save(AccountHistory.recordWithdrawHistory(fromAccount, fromAccount, money));
	}

	private void recordCompletionTransferMoney(Account fromAccount, Account toAccount, Money money) {
		accountHistoryRepository.save(AccountHistory.recordWithdrawHistory(fromAccount, toAccount, money));
		accountHistoryRepository.save(AccountHistory.recordDepositHistory(toAccount, fromAccount, money));
	}
}
