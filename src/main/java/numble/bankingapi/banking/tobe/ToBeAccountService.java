package numble.bankingapi.banking.tobe;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.banking.domain.Account;
import numble.bankingapi.banking.domain.AccountHistory;
import numble.bankingapi.banking.domain.AccountHistoryRepository;
import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.AccountRepository;
import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.util.generator.AccountNumberGenerator;

@Service
@RequiredArgsConstructor
public class ToBeAccountService {
	private final AccountRepository accountRepository;

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
		Account account = getAccountByAccountNumberWithOptimisticLock(accountNumber);
		account.deposit(money);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void withdrawMoney(AccountNumber accountNumber, Money money) {
		Account account = getAccountByAccountNumberWithOptimisticLock(accountNumber);
		account.withdraw(money);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void transferMoney(AccountNumber fromAccountNumber, AccountNumber toAccountNumber,
		Money money) {
		Account fromAccount = getAccountByAccountNumberWithOptimisticLock(fromAccountNumber);
		Account toAccount = getAccountByAccountNumberWithOptimisticLock(toAccountNumber);

		if (fromAccount.equals(toAccount)) {
			throw new IllegalArgumentException();
		}

		fromAccount.withdraw(money);
		toAccount.deposit(money);
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public List<Account> getFriendAccounts(List<Long> friendIds) {
		return accountRepository.findAllByUserIdIn(friendIds)
			.stream()
			.toList();
	}

	private Account getAccountByAccountNumberWithOptimisticLock(AccountNumber accountNumber) {
		return accountRepository.findByAccountNumberWithOptimisticLock(accountNumber).orElseThrow();
	}
}
