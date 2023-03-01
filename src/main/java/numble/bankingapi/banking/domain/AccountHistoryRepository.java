package numble.bankingapi.banking.domain;

import java.util.List;

public interface AccountHistoryRepository {
	<S extends AccountHistory> S save(S entity);

	List<AccountHistory> findByFromAccountNumber(AccountNumber accountNumber);
}
