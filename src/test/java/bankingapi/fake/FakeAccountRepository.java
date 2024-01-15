package bankingapi.fake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import bankingapi.banking.domain.Account;
import bankingapi.banking.domain.AccountNumber;
import bankingapi.banking.domain.AccountRepository;

public class FakeAccountRepository implements AccountRepository {

	Map<Long, Account> maps = new HashMap<>();
	private Long sequence = 0L;

	@Override
	public Optional<Account> findById(Long id) {
		return Optional.ofNullable(maps.get(id));
	}

	@Override
	public Optional<Account> findByAccountNumber(AccountNumber accountNumber) {
		return maps.values().stream().filter(account -> account.getAccountNumber().equals(accountNumber)).findFirst();
	}

	@Override
	public Optional<Account> findByAccountNumberWithOptimisticLock(AccountNumber accountNumber) {
		return maps.values().stream().filter(account -> account.getAccountNumber().equals(accountNumber)).findFirst();
	}

	@Override
	public Account save(Account entity) {
		var id = ++sequence;
		var account = new Account(id, entity.getUserId(), entity.getAccountNumber(),
			entity.getBalance());
		maps.put(id, account);
		return account;
	}

	@Override
	public void flush() {

	}

	@Override
	public List<Account> findAll() {
		return maps.values().stream().toList();
	}

	@Override
	public List<Account> findAllByUserIdIn(List<Long> userIds) {
		return maps.values()
			.stream()
			.filter(account -> userIds.contains(account.getUserId()))
			.collect(Collectors.toList());
	}
}
