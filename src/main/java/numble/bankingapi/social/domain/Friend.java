package numble.bankingapi.social.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Friend {
	private Long id;
	private Long fromMemberId;
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
