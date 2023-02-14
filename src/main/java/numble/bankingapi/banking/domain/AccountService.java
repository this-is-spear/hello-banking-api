package numble.bankingapi.banking.domain;

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
		return getAccountById(id);
	}

	@Transactional
	public void depositMoney(AccountNumber accountNumber, Money money) {
		Account account = getAccountByAccountNumber(accountNumber);
		account.deposit(money);
	}

	@Transactional
	public void withdrawMoney(AccountNumber accountNumber, Money money) {
		Account account = getAccountByAccountNumber(accountNumber);
		account.withdraw(money);
	}

	@Transactional
	public void transferMoney(AccountNumber fromAccountNumber, AccountNumber toAccountNumber, Money money) {
		Account fromAccount = getAccountByAccountNumber(fromAccountNumber);
		Account toAccount = getAccountByAccountNumber(toAccountNumber);
		fromAccount.withdraw(money);
		toAccount.deposit(money);
	}

	private Account getAccountById(Long id) {
		return accountRepository.findById(id).orElseThrow();
	}

	private Account getAccountByAccountNumber(AccountNumber accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber).orElseThrow();
	}
}
