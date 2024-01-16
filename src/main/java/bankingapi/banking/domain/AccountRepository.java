package bankingapi.banking.domain;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
	Optional<Account> findById(Long id);

	Optional<Account> findByAccountNumber(AccountNumber accountNumber);

	Optional<Account> findByAccountNumberWithOptimisticLock(AccountNumber accountNumber);

	<S extends Account> S save(S entity);

	void flush();

	List<Account> findAll();

	List<Account> findAllByUserIdIn(List<Long> userId);
}
