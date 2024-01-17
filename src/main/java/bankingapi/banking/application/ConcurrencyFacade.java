package bankingapi.banking.application;

import bankingapi.banking.domain.AccountNumber;
import bankingapi.banking.domain.AccountService;
import bankingapi.banking.domain.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import bankingapi.concurrency.ConcurrencyManager;

@Service
@RequiredArgsConstructor
public class ConcurrencyFacade {
	private final ConcurrencyManager concurrencyManager;
	private final AccountService accountService;

	public void transferWithLock(AccountNumber accountNumber, AccountNumber toAccountNumber,
                                 Money amount) {
		concurrencyManager.executeWithLock(accountNumber.getNumber(), toAccountNumber.getNumber(),
			() -> accountService.transferMoney(accountNumber, toAccountNumber, amount)
		);
	}
	public void depositWithLock(AccountNumber accountNumber, Money amount) {
		concurrencyManager.executeWithLock(accountNumber.getNumber(), () -> {
			accountService.depositMoney(accountNumber, amount);
		});
	}

	public void withdrawWithLock(AccountNumber accountNumber, Money amount) {
		concurrencyManager.executeWithLock(accountNumber.getNumber(), () -> {
			accountService.withdrawMoney(accountNumber, amount);
		});
	}
}
