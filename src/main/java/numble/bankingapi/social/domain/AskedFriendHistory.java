package numble.bankingapi.social.domain;

public class AskedFriendHistory {
	private Long id;
	private Long fromMemberId;
	private Long toMemberId;
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
