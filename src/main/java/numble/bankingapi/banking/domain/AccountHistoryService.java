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
	public void recordCompletionDepositMoney(AccountNumber fromAccountNumber, Money money) {
		accountHistoryRepository.save(
			new AccountHistory(fromAccountNumber, fromAccountNumber, HistoryType.DEPOSIT, money));
	}

	@Transactional
	public void recordCompletionWithdrawMoney(AccountNumber fromAccountNumber,
		Money money) {
		accountHistoryRepository.save(
			new AccountHistory(fromAccountNumber, fromAccountNumber, HistoryType.WITHDRAW, money));
	}

	@Transactional
	public void recordCompletionTransferMoney(AccountNumber fromAccountNumber, AccountNumber toAccountNumber,
		Money money) {
		accountHistoryRepository.save(
			new AccountHistory(fromAccountNumber, toAccountNumber, HistoryType.WITHDRAW, money));
		accountHistoryRepository.save(
			new AccountHistory(toAccountNumber, fromAccountNumber, HistoryType.DEPOSIT, money));
	}

	public List<AccountHistory> findByFromAccountNumber(AccountNumber accountNumber) {
		return accountHistoryRepository.findByFromAccountNumber(accountNumber);
	}

}
