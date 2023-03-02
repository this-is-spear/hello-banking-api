package numble.bankingapi.social.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class FriendTest {

	@Test
	@DisplayName("식별자(Id), 사용자 식별자(FromUserId), 상대방 식별자(ToUserId)를 포함한다.")
	void createFriend() {
		assertDoesNotThrow(
			() -> new Friend(3L, 5L)
		);
	}

	@ParameterizedTest
	@NullSource
	@DisplayName("사용자 식별자는 비어있을 수 없다.")
	void createFriend_fromMemberIdNotNull(Long invalidFromUserId) {
		assertThatThrownBy(
			() -> new Friend(invalidFromUserId, 3L)
		).isInstanceOf(NullPointerException.class);
	}

	@ParameterizedTest
	@NullSource
	@DisplayName("상대방 식별자는 비어있을 수 없다.")
	void createFriend_toMemberIdNotNull(Long invalidToUserId) {
		assertThatThrownBy(
			() -> new Friend(3L, invalidToUserId)
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("사용자 식별자와 상대방 식별자는 같을 수 없다.")
	void createFriend_notEqualsFromMemberIdAndToMemberId() {
		var memberId = 3L;

		assertThatThrownBy(
			() -> new Friend(memberId, memberId)
		).isInstanceOf(IllegalArgumentException.class);
	}
}
