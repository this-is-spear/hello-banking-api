package numble.bankingapi.social.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AskedFriendHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "fromMemberId", nullable = false)
	private Long fromMemberId;
	@Column(name = "toMemberId", nullable = false)
	private Long toMemberId;
	@Enumerated(EnumType.STRING)
	private ApprovalStatus status;

	public AskedFriendHistory(Long fromMemberId, Long toMemberId, ApprovalStatus status) {
		validateFromMemberId(fromMemberId);
		validateToMemberId(toMemberId);
		if (fromMemberId.equals(toMemberId)) {
			throw new IllegalArgumentException();
		}

		this.fromMemberId = fromMemberId;
		this.toMemberId = toMemberId;
		this.status = status;
	}

	public AskedFriendHistory(Long fromMemberId, Long toMemberId) {
		this(fromMemberId, toMemberId, ApprovalStatus.WAITING);
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

	public void approve() {
		if (!this.status.equals(ApprovalStatus.WAITING)) {
			throw new IllegalArgumentException();
		}
		this.status = ApprovalStatus.APPROVED;
	}

	public void reject() {
		if (!this.status.equals(ApprovalStatus.WAITING)) {
			throw new IllegalArgumentException();
		}
		this.status = ApprovalStatus.REJECTED;
	}

	public void waitAgain() {
		if (!this.status.equals(ApprovalStatus.REJECTED)) {
			throw new IllegalArgumentException();
		}
		this.status = ApprovalStatus.WAITING;
	}
}
