package numble.bankingapi.banking.domain;

import java.util.Optional;

public interface AccountRepository {
	Optional<Account> findById(Long id);

	Optional<Account> findByAccountNumber(AccountNumber accountNumber);

	<S extends Account> S save(S entity);
}