package numble.bankingapi.idempotent.domain;

import org.springframework.http.HttpStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotentRequestHistory {
	@Id
	@Column(name = "id")
	@EqualsAndHashCode.Include
	private String idempotentId;
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private HttpStatus responseStatus;
	@Column(name = "body")
	private String responseBody;

	public IdempotentRequestHistory(String idempotentId, HttpStatus responseStatus, String responseBody) {
		validId(idempotentId);
		validStatus(responseStatus);

		this.idempotentId = idempotentId;
		this.responseStatus = responseStatus;
		this.responseBody = responseBody;
	}

	public IdempotentRequestHistory(String idempotentId, HttpStatus responseStatus) {
		this(idempotentId, responseStatus, null);
	}

	private void validId(String idempotentId) {
		if (idempotentId == null || idempotentId.isBlank()) {
			throw new IllegalArgumentException();
		}
	}

	private void validStatus(HttpStatus responseStatus) {
		if (responseStatus == null) {
			throw new IllegalArgumentException();
		}
	}
}
