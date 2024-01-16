package bankingapi.idempotent.domain;

import bankingapi.common.BaseEntity;
import org.springframework.http.HttpStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotentRequestHistory extends BaseEntity {
	@Id
	@Column(name = "id")
	@ToString.Include
	@EqualsAndHashCode.Include
	private String idempotentId;
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private HttpStatus responseStatus;

	public IdempotentRequestHistory(String idempotentId, HttpStatus responseStatus) {
		validId(idempotentId);
		validStatus(responseStatus);

		this.idempotentId = idempotentId;
		this.responseStatus = responseStatus;
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
