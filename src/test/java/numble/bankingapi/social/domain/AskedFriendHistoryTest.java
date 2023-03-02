package numble.bankingapi.social.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

class AskedFriendHistoryTest {

	private AskedFriendHistory 대기중인_요청;
	private AskedFriendHistory 거절된_요청;

	@BeforeEach
	void setUp() {
		대기중인_요청 = new AskedFriendHistory(2L, 3L, ApprovalStatus.WAITING);
		거절된_요청 = new AskedFriendHistory(2L, 3L, ApprovalStatus.REJECTED);
	}

	@ParameterizedTest
	@EnumSource(ApprovalStatus.class)
	@DisplayName("식별자(Id), 사용자 식별자(FromMemberId), 상대방 식별자(ToMemberId), 승인 여부(ApprovalStatus)를 포함한다.")
	void createdAskedFriendHistory(ApprovalStatus status) {
		assertDoesNotThrow(
			() -> new AskedFriendHistory(2L, 3L, status)
		);
	}

	@ParameterizedTest
	@NullSource
	@DisplayName("사용자 식별자는 비어있을 수 없다.")
	void createdAskedFriendHistory_fromMemberIdNotNull(Long invalidFromMemberId) {
		assertThatThrownBy(
			() -> new AskedFriendHistory(invalidFromMemberId, 3L, ApprovalStatus.WAITING)
		).isInstanceOf(NullPointerException.class);
	}

	@ParameterizedTest
	@NullSource
	@DisplayName("상대방 식별자는 비어있을 수 없다.")
	void createdAskedFriendHistory_toMemberIdNotNull(Long invalidToMemberId) {
		assertThatThrownBy(
			() -> new AskedFriendHistory(2L, invalidToMemberId, ApprovalStatus.WAITING)
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("사용자 식별자와 상대방 식별자는 같을 수 없다.")
	void createdAskedFriendHistory_notEqualsFromMemberIdAndToMemberId() {
		assertThatThrownBy(
			() -> new AskedFriendHistory(2L, 2L, ApprovalStatus.WAITING)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("대기(WAITING)에서 승인(APPROVED)으로 변경한다.")
	void statusApprove() {
		assertDoesNotThrow(
			대기중인_요청::approve
		);
	}

	@ParameterizedTest
	@EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"WAITING"})
	@DisplayName("승인(APPROVED)되는 경우 승인 여부(ApprovalStatus)가 대기(WAITING)여야 한다.")
	void statusApprove_mustBeWaiting(ApprovalStatus notWaiting) {
		var 대기중이지_않은_요청 = new AskedFriendHistory(2L, 3L, notWaiting);

		assertThatThrownBy(
			대기중이지_않은_요청::approve
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("대기(WAITING)에서 거절(REJECTED)로 변경한다.")
	void statusReject() {
		assertDoesNotThrow(
			대기중인_요청::reject
		);
	}

	@ParameterizedTest
	@EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"WAITING"})
	@DisplayName("거절(REJECTED)되는 경우 승인 여부(ApprovalStatus)가 대기(WAITING)여야 한다.")
	void statusReject_mustBeWaiting(ApprovalStatus notWaiting) {
		var 대기중이지_않은_요청 = new AskedFriendHistory(2L, 3L, notWaiting);

		assertThatThrownBy(
			대기중이지_않은_요청::reject
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("거절(REJECTED)에서 대기(WAITING)로 변경한다.")
	void statusWait() {
		assertDoesNotThrow(
			거절된_요청::waitAgain
		);
	}

	@ParameterizedTest
	@EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"REJECTED"})
	@DisplayName("대기(WAITING) 상태로 변경하는 경우 승인 여부(ApprovalStatus)가 거절(REJECTED)이어야 한다.")
	void statusWait_mustBeRejected(ApprovalStatus notRejected) {
		var 거절되지_않은_요청 = new AskedFriendHistory(2L, 3L, notRejected);

		assertThatThrownBy(
			거절되지_않은_요청::waitAgain
		).isInstanceOf(IllegalArgumentException.class);
	}
}
