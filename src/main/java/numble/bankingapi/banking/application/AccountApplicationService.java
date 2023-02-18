package numble.bankingapi.banking.application;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.banking.domain.Account;
import numble.bankingapi.banking.domain.AccountHistory;
import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.AccountService;
import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.banking.dto.HistoryResponse;
import numble.bankingapi.banking.dto.HistoryResponses;
import numble.bankingapi.banking.dto.TargetResponse;
import numble.bankingapi.banking.dto.TargetResponses;
import numble.bankingapi.banking.dto.TransferCommand;

@Service
@RequiredArgsConstructor
public class AccountApplicationService {
	private final AccountService accountService;
	private final ConcurrencyFacade concurrencyFacade;

	public HistoryResponses getHistory(String stringAccountNumber) {
		AccountNumber accountNumber = getAccountNumber(stringAccountNumber);
		Account account = accountService.getAccountByAccountNumber(accountNumber);
		return new HistoryResponses(account.getBalance(),
			accountService.findAccountHistoriesByFromAccountNumber(accountNumber)
				.stream().map(this::getHistoryResponse).collect(Collectors.toList())
		);
	}

	public void deposit(String number, Money money) {
		AccountNumber accountNumber = getAccountNumber(number);
		accountService.depositMoney(accountNumber, money);
	}

	public void withdraw(String number, Money money) {
		AccountNumber accountNumber = getAccountNumber(number);
		accountService.withdrawMoney(accountNumber, money);
	}

	public void transfer(String accountNumber, TransferCommand command) {
		AccountNumber fromAccountNumber = getAccountNumber(accountNumber);
		AccountNumber toAccountNumber = getAccountNumber(command.toAccountNumber());

		Money money = command.amount();
		concurrencyFacade.transferWithLock(fromAccountNumber, toAccountNumber, money);
	}

	public TargetResponses getTargets() {
		return new TargetResponses(accountService.findAll()
			.stream().map(this::getTargetResponse)
			.collect(Collectors.toList()));
	}

	private HistoryResponse getHistoryResponse(AccountHistory accountHistory) {
		return new HistoryResponse(accountHistory.getType(), accountHistory.getMoney(),
			accountHistory.getFromAccountNumber(), accountHistory.getToAccountNumber(),
			accountHistory.getCreatedDate());
	}

	private TargetResponse getTargetResponse(Account account) {
		return new TargetResponse(account.getAccountNumber());
	}

	private AccountNumber getAccountNumber(String accountNumber) {
		return new AccountNumber(accountNumber);
	}
}
