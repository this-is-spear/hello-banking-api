package numble.bankingapi.idempotent.domain;

import java.util.Optional;

import numble.bankingapi.idempotent.domain.IdempotentRequestHistory;

public interface IdempotentRequestRepository {
	<S extends IdempotentRequestHistory> S save(S entity);

	Optional<IdempotentRequestHistory> findById(String s);
}
