package numble.bankingapi.idempotent.domain;

import java.util.Optional;

public interface IdempotentRequestRepository {
	<S extends IdempotentRequestHistory> S save(S entity);

	Optional<IdempotentRequestHistory> findById(String s);
}
