package numble.bankingapi.banking.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountHistoryService {
	private final AccountHistoryRepository accountHistoryRepository;

	@Transactional
	public void recordCompletionDepositMoney(Account fromAccount, Money money) {
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(fromAccount.getAccountNumber())
				.toAccountNumber(fromAccount.getAccountNumber())
				.type(HistoryType.DEPOSIT)
				.money(money)
				.balance(fromAccount.getBalance().plus(money))
				.build());
	}

	@Transactional
	public void recordCompletionWithdrawMoney(Account fromAccount, Money money) {
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(fromAccount.getAccountNumber())
				.toAccountNumber(fromAccount.getAccountNumber())
				.type(HistoryType.WITHDRAW)
				.money(money)
				.balance(fromAccount.getBalance().minus(money))
				.build());
	}

	@Transactional
	public void recordCompletionTransferMoney(Account fromAccount, Account toAccount, Money money) {
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(fromAccount.getAccountNumber())
				.toAccountNumber(toAccount.getAccountNumber())
				.type(HistoryType.WITHDRAW)
				.money(money)
				.balance(fromAccount.getBalance().minus(money))
				.build());
		accountHistoryRepository.save(
			AccountHistory.builder()
				.fromAccountNumber(toAccount.getAccountNumber())
				.toAccountNumber(fromAccount.getAccountNumber())
				.type(HistoryType.DEPOSIT)
				.money(money)
				.balance(fromAccount.getBalance().plus(money))
				.build());
	}

	public List<AccountHistory> findByFromAccountNumber(AccountNumber accountNumber) {
		return accountHistoryRepository.findByFromAccountNumber(accountNumber);
	}

}
