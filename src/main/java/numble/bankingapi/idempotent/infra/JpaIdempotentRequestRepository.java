package numble.bankingapi.idempotent.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import numble.bankingapi.idempotent.domain.IdempotentRequestHistory;
import numble.bankingapi.idempotent.domain.IdempotentRequestRepository;

@Repository
public interface JpaIdempotentRequestRepository
	extends JpaRepository<IdempotentRequestHistory, String>, IdempotentRequestRepository {
	@Override
	<S extends IdempotentRequestHistory> S save(S entity);

	@Override
	Optional<IdempotentRequestHistory> findById(String s);
}
