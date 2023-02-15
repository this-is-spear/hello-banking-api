package numble.bankingapi.banking.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.util.AccountNumberGenerator;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;

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
	public void depositMoney(Account account, Money money) {
		executeDepositWithLock(account, money);
	}

	@Transactional
	public void withdrawMoney(Account account, Money money) {
		executeWithdrawWithLock(account, money);
	}

	@Transactional
	public void transferMoney(Account fromAccount, Account toAccount, Money money) {
		executeWithdrawWithLock(fromAccount, money);
		executeDepositWithLock(toAccount, money);
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	private void executeWithdrawWithLock(Account account, Money money) {
		account.withdraw(money);
		accountRepository.save(account);
	}

	private void executeDepositWithLock(Account account, Money money) {
		account.deposit(money);
		accountRepository.save(account);
	}
}
