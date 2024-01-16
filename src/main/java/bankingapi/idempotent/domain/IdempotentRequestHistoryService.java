package bankingapi.idempotent.domain;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdempotentRequestHistoryService {
	private final IdempotentRequestRepository idempotentRequestRepository;

	public void save(IdempotentRequestHistory idempotentRequestHistory) {
		idempotentRequestRepository.save(idempotentRequestHistory);
	}

	public boolean isPresent(String id) {
		return idempotentRequestRepository.findById(id).isPresent();
	}

	public IdempotentRequestHistory getIdempotentRequestHistory(String id) {
		return idempotentRequestRepository.findById(id).orElseThrow(IllegalArgumentException::new);
	}
}
