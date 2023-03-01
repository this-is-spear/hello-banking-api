package numble.bankingapi.banking.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.banking.domain.AccountService;
import numble.bankingapi.concurrency.ConcurrencyManager;

@Service
@RequiredArgsConstructor
public class ConcurrencyFacade {
	private final ConcurrencyManager concurrencyManager;
	private final AccountService accountService;

	@Transactional
	public void transferWithLock(AccountNumber accountNumber, AccountNumber toAccountNumber,
		Money amount) {
		concurrencyManager.executeWithLock(
			accountNumber.getNumber(),
			() -> accountService.transferMoney(accountNumber, toAccountNumber, amount)
		);
	}

	@Transactional
	public void depositWithLock(AccountNumber accountNumber, Money amount) {
		concurrencyManager.executeWithLock(
			accountNumber.getNumber(),
			() -> accountService.depositMoney(accountNumber, amount)
		);
	}

	@Transactional
	public void withdrawWithLock(AccountNumber accountNumber, Money amount) {
		concurrencyManager.executeWithLock(
			accountNumber.getNumber(),
			() -> accountService.withdrawMoney(accountNumber, amount)
		);
	}
}
