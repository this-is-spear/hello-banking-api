package numble.bankingapi.banking.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import numble.bankingapi.banking.domain.AccountHistory;
import numble.bankingapi.banking.domain.AccountHistoryRepository;
import numble.bankingapi.banking.domain.AccountNumber;

@Repository
public interface JpaAccountHistoryRepository extends JpaRepository<AccountHistory, Long>, AccountHistoryRepository {
	@Override
	<S extends AccountHistory> S save(S entity);

	List<AccountHistory> findByFromAccountNumber(AccountNumber accountNumber);
}
