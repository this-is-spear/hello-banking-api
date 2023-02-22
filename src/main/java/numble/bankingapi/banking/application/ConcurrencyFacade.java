package numble.bankingapi.banking.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.AccountService;
import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.concurrency.ConcurrencyManager;

@Service
@RequiredArgsConstructor
public class ConcurrencyFacade {
	private final ConcurrencyManager concurrencyManager;
	private final AccountService accountService;

	public void transferWithLock(String principal, AccountNumber accountNumber, AccountNumber toAccountNumber,
		Money amount) {
		concurrencyManager.executeWithLock(
			accountNumber.getNumber(),
			() -> accountService.transferMoney(principal, accountNumber, toAccountNumber, amount)
		);
	}

	public void depositWithLock(String principal, AccountNumber accountNumber, Money amount) {
		concurrencyManager.executeWithLock(
			accountNumber.getNumber(),
			() -> accountService.depositMoney(principal, accountNumber, amount)
		);
	}

	public void withdrawWithLock(String principal, AccountNumber accountNumber, Money amount) {
		concurrencyManager.executeWithLock(
			accountNumber.getNumber(),
			() -> accountService.withdrawMoney(principal, accountNumber, amount)
		);
	}
}
