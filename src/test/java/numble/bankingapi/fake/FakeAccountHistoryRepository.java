package numble.bankingapi.fake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import numble.bankingapi.banking.domain.AccountHistory;
import numble.bankingapi.banking.domain.AccountHistoryRepository;
import numble.bankingapi.banking.domain.AccountNumber;

public class FakeAccountHistoryRepository implements AccountHistoryRepository {
	Map<Long, AccountHistory> maps = new HashMap<>();
	private Long sequence = 0L;

	@Override
	public AccountHistory save(AccountHistory entity) {
		var id = ++sequence;
		var accountHistory = new AccountHistory(id, entity.getFromAccountNumber(),
			entity.getToAccountNumber(), entity.getMoney(),
			entity.getBalance(), entity.getType());
		maps.put(id, accountHistory);
		return accountHistory;
	}

	@Override
	public List<AccountHistory> findByFromAccountNumber(AccountNumber accountNumber) {
		return maps.values()
			.stream()
			.filter(accountHistory -> accountHistory.getFromAccountNumber().equals(accountNumber))
			.toList();
	}
}
