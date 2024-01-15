package bankingapi.banking.infra;

import java.util.List;
import java.util.Optional;

import bankingapi.banking.domain.AccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import bankingapi.banking.domain.Account;
import bankingapi.banking.domain.AccountRepository;

@Repository
public interface JpaAccountRepository extends JpaRepository<Account, Long>, AccountRepository {
	@Override
	Optional<Account> findById(Long id);

	@Override
	Optional<Account> findByAccountNumber(AccountNumber accountNumber);

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select a from Account a where a.accountNumber = :accountNumber")
	Optional<Account> findByAccountNumberWithOptimisticLock(AccountNumber accountNumber);

	@Override
	<S extends Account> S save(S entity);

	@Override
	void flush();

	@Override
	List<Account> findAll();

	@Query("select a from Account a where a.userId in :userId")
	List<Account> findAllByUserIdIn(List<Long> userId);

}
