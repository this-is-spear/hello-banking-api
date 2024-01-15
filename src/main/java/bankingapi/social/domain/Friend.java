package bankingapi.social.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "fromMemberId", nullable = false)
	private Long fromMemberId;
	@Column(name = "toMemberId", nullable = false)
	private Long toMemberId;

	@Builder
	public Friend(Long fromMemberId, Long toMemberId) {
		validateFromMemberId(fromMemberId);
		validateToMemberId(toMemberId);
		if (fromMemberId.equals(toMemberId)) {
			throw new IllegalArgumentException();
		}
		this.fromMemberId = fromMemberId;
		this.toMemberId = toMemberId;
	}

	private void validateFromMemberId(Long fromMemberId) {
		if (fromMemberId == null) {
			throw new NullPointerException();
		}
	}

	private void validateToMemberId(Long toMemberId) {
		if (toMemberId == null) {
			throw new NullPointerException();
		}
	}
}
