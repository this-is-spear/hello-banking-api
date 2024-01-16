package bankingapi.banking.infra;

import java.util.List;

import bankingapi.banking.domain.AccountHistory;
import bankingapi.banking.domain.AccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bankingapi.banking.domain.AccountHistoryRepository;

@Repository
public interface JpaAccountHistoryRepository extends JpaRepository<AccountHistory, Long>, AccountHistoryRepository {
	@Override
	<S extends AccountHistory> S save(S entity);

	List<AccountHistory> findByFromAccountNumber(AccountNumber accountNumber);
}
