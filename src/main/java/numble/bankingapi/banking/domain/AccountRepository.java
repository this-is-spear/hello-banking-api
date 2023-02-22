package numble.bankingapi.banking.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AccountRepository {
	Optional<Account> findById(Long id);

	Optional<Account> findByAccountNumber(AccountNumber accountNumber);

	<S extends Account> S save(S entity);

	List<Account> findAll();

	List<Account> findAllByUserIdIn(List<Long> userId);
}
