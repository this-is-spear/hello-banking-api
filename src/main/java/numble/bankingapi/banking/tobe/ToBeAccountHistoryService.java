package numble.bankingapi.banking.tobe;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.banking.domain.Account;
import numble.bankingapi.banking.domain.AccountHistory;
import numble.bankingapi.banking.domain.AccountHistoryRepository;
import numble.bankingapi.banking.domain.HistoryType;
import numble.bankingapi.banking.domain.Money;

@Service
@RequiredArgsConstructor
public class ToBeAccountHistoryService {
	private final AccountHistoryRepository accountHistoryRepository;

	public List<AccountHistory> findAccountHistoriesByFromAccountNumber(Account account) {
		return accountHistoryRepository.findByFromAccountNumber(account.getAccountNumber());
	}

	public void recordCompletionDepositMoney(Account fromAccount, Money money) {
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(fromAccount.getAccountNumber())
				.toAccountNumber(fromAccount.getAccountNumber())
				.type(HistoryType.DEPOSIT)
				.money(money)
				.balance(fromAccount.getBalance())
				.build());
	}

	public void recordCompletionWithdrawMoney(Account fromAccount, Money money) {
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(fromAccount.getAccountNumber())
				.toAccountNumber(fromAccount.getAccountNumber())
				.type(HistoryType.WITHDRAW)
				.money(money)
				.balance(fromAccount.getBalance())
				.build());
	}

	public void recordCompletionTransferMoney(Account fromAccount, Account toAccount, Money money) {
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
