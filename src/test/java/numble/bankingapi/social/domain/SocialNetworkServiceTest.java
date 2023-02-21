package numble.bankingapi.social.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberRepository;
import numble.bankingapi.social.dto.AskedFriendResponses;
import numble.bankingapi.social.dto.FriendResponses;

@Transactional
@SpringBootTest
class SocialNetworkServiceTest {

	@Autowired
	private FriendRepository friendRepository;
	@Autowired
	private AskedFriendHistoryRepository askedFriendHistoryRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private SocialNetworkService socialNetworkService;
	private Member 보낸_사용자;
	private Member 받은_사용자;
	private Member 익명의_사용자;

	@BeforeEach
	void setUp() {
		보낸_사용자 = memberRepository.save(new Member("this-member@email.com", "member1", "password"));
		받은_사용자 = memberRepository.save(new Member("this-other@email.com", "member2", "password"));
		익명의_사용자 = memberRepository.save(new Member("this-anonymout@email.com", "member3", "password"));
	}

	@Test
	@DisplayName("사용자 정보(Principal)와 상대방의 식별자(MemberId)를 입력받아 친구 요청을 보낸다.")
	void askWantToBefriends() {
		assertDoesNotThrow(
			() -> socialNetworkService.askWantToBefriends(보낸_사용자.getEmail(), 받은_사용자.getId())
		);
	}

	@Test
	@DisplayName("친구 요청을 보낼때, 사용자의 정보가 존재하지 않으면 예외가 발생한다.")
	void askWantToBefriends_fromMemberNotNull() {
		String invalidEmail = "invalid-member@email.com";

		assertThatThrownBy(
			() -> socialNetworkService.askWantToBefriends(invalidEmail, 받은_사용자.getId())
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("친구 요청을 보낼때, 상대방의 정보가 존재하지 않으면 예외가 발생한다.")
	void askWantToBefriends_toMemberNotNull() {
		long invalidId = 999L;

		assertThatThrownBy(
			() -> socialNetworkService.askWantToBefriends(보낸_사용자.getEmail(), invalidId)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("사용자 정보(Principal))와 친구 요청 식별자(AskedFriendHistoryId)를 입력해 친구 요청을 승낙한다.")
	void approvalRequest() {
		AskedFriendHistory 대기중인_친구추가_요청 = askedFriendHistoryRepository.save(
			new AskedFriendHistory(보낸_사용자.getId(), 받은_사용자.getId(), ApprovalStatus.WAITING));

		assertDoesNotThrow(
			() -> socialNetworkService.approvalRequest(받은_사용자.getEmail(), 대기중인_친구추가_요청.getId())
		);

		AskedFriendHistory 친구추가_요청 = askedFriendHistoryRepository.findById(대기중인_친구추가_요청.getId()).get();
		assertThat(친구추가_요청.getStatus()).isEqualTo(ApprovalStatus.APPROVED);

	}

	@Test
	@DisplayName("승인하는 사람은 요청 받은 사용자 본인이 아니면 예외가 발생한다.")
	void approvalRequest_equalsToMemberAndNowMember() {
		String 받은_사용자가_아님 = 보낸_사용자.getEmail();
		AskedFriendHistory 대기중인_친구추가_요청 = askedFriendHistoryRepository.save(
			new AskedFriendHistory(보낸_사용자.getId(), 받은_사용자.getId(), ApprovalStatus.WAITING));

		assertThatThrownBy(
			() -> socialNetworkService.approvalRequest(받은_사용자가_아님, 대기중인_친구추가_요청.getId())
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("친구 요청을 승인할 때, 친구 요청 정보가 존재하지 않으면 예외가 발생한다.")
	void approvalRequest_notEmpty() {
		long 존재하지않는_요청 = 999L;

		assertThatThrownBy(
			() -> socialNetworkService.approvalRequest(받은_사용자.getEmail(), 존재하지않는_요청)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("친구 요청을 승인하면 친구 정보가 생성된다.")
	void approvalRequest_getFriend() {
		AskedFriendHistory 대기중인_친구추가_요청 = askedFriendHistoryRepository.save(
			new AskedFriendHistory(보낸_사용자.getId(), 받은_사용자.getId(), ApprovalStatus.WAITING));

		assertDoesNotThrow(
			() -> socialNetworkService.approvalRequest(받은_사용자.getEmail(), 대기중인_친구추가_요청.getId())
		);
		assertAll(
			() -> assertThat(friendRepository.findByFromMemberId(보낸_사용자.getId())
				.stream()
				.map(friend -> friend.getToMemberId().equals(받은_사용자.getId()))
				.findFirst()
				.isPresent()).isTrue(),
			() -> assertThat(friendRepository.findByFromMemberId(받은_사용자.getId())
				.stream()
				.map(friend -> friend.getToMemberId().equals(보낸_사용자.getId()))
				.findFirst()
				.isPresent()).isTrue()
		);
	}

	@Test
	@DisplayName("사용자 정보(Principal)와 친구 요청 식별자(AskedFriendHistoryId)를 입력해 친구 요청을 거절한다.")
	void rejectRequest() {
		AskedFriendHistory 대기중인_요청 = askedFriendHistoryRepository.save(
			new AskedFriendHistory(보낸_사용자.getId(), 받은_사용자.getId(), ApprovalStatus.WAITING));

		assertDoesNotThrow(
			() -> socialNetworkService.rejectRequest(받은_사용자.getEmail(), 대기중인_요청.getId())
		);
	}

	@Test
	@DisplayName("거절하는 사람은 요청 받은 사용자 본인이 아니면 예외가 발생한다.")
	void rejectRequest_equalsToMemberAndNowMember() {
		String 받은_사용자가_아님 = 보낸_사용자.getEmail();
		AskedFriendHistory 대기중인_친구추가_요청 = askedFriendHistoryRepository.save(
			new AskedFriendHistory(보낸_사용자.getId(), 받은_사용자.getId(), ApprovalStatus.WAITING));

		assertThatThrownBy(
			() -> socialNetworkService.rejectRequest(받은_사용자가_아님, 대기중인_친구추가_요청.getId())
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("친구 요청을 승인할 때, 친구 요청 정보가 존재하지 않으면 예외가 발생한다.")
	void rejectRequest_notEmpty() {
		long 존재하지않는_요청 = 999L;
		assertThatThrownBy(
			() -> socialNetworkService.rejectRequest(받은_사용자.getEmail(), 존재하지않는_요청)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("자신의 정보(Principal)를 이용해 친구 목록을 조회한다.")
	void findFriends() {
		friendRepository.save(new Friend(보낸_사용자.getId(), 받은_사용자.getId()));
		friendRepository.save(new Friend(보낸_사용자.getId(), 익명의_사용자.getId()));
		friendRepository.save(new Friend(보낸_사용자.getId(), 100L));

		FriendResponses responses = assertDoesNotThrow(
			() -> socialNetworkService.findFriends(보낸_사용자.getEmail())
		);
		assertThat(responses.friendResponses()).hasSize(2);
	}

	@Test
	@DisplayName("자신의 정보(Principal)를 이용해 친구 목록을 조회한다.")
	void findRequestWandToBeFriend() {
		AskedFriendHistory 첫_번째_요청 = new AskedFriendHistory(보낸_사용자.getId(), 받은_사용자.getId(), ApprovalStatus.WAITING);
		AskedFriendHistory 두_번째_요청 = new AskedFriendHistory(익명의_사용자.getId(), 받은_사용자.getId(), ApprovalStatus.WAITING);
		askedFriendHistoryRepository.save(첫_번째_요청);
		askedFriendHistoryRepository.save(두_번째_요청);

		AskedFriendResponses friendResponses = assertDoesNotThrow(
			() -> socialNetworkService.findRequestWandToBeFriend(받은_사용자.getEmail())
		);
		assertThat(friendResponses.askedFriendResponses()).hasSize(2);
	}

	@Test
	@DisplayName("친구 요청 목록을 조회할 때, ")
	void findRequestWandToBeFriend_noInvalidUser() {
		long 유효하지_않은_사용자 = 999L;
		AskedFriendHistory 첫_번째_요청 = new AskedFriendHistory(보낸_사용자.getId(), 받은_사용자.getId(), ApprovalStatus.WAITING);
		AskedFriendHistory 두_번째_요청 = new AskedFriendHistory(유효하지_않은_사용자, 받은_사용자.getId(), ApprovalStatus.WAITING);
		askedFriendHistoryRepository.save(첫_번째_요청);
		askedFriendHistoryRepository.save(두_번째_요청);

		assertThatThrownBy(
			() -> socialNetworkService.findRequestWandToBeFriend(받은_사용자.getEmail())
		).isInstanceOf(IllegalArgumentException.class);
	}
}
