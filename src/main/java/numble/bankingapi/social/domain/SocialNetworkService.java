package numble.bankingapi.social.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberService;
import numble.bankingapi.social.dto.AskedFriendResponse;
import numble.bankingapi.social.dto.AskedFriendResponses;
import numble.bankingapi.social.dto.FriendResponse;
import numble.bankingapi.social.dto.FriendResponses;

@Service
@RequiredArgsConstructor
public class SocialNetworkService {

	private final FriendService friendService;
	private final MemberService memberService;

	@Transactional
	public void askWantToBefriends(String principal, Long toMemberId) {
		final var fromMember = getMember(principal);
		final var  toMember = memberService.findById(toMemberId);
		final var  waitingAskedFriend = new AskedFriendHistory(fromMember.getId(), toMember.getId());
		friendService.saveAskedFriendHistory(waitingAskedFriend);
	}

	@Transactional
	public void approvalRequest(String principal, Long requestId) {
		final var  toMember = getMember(principal);
		var askedFriendHistory = friendService.findFriendHistoryById(requestId);

		if (!askedFriendHistory.getToMemberId().equals(toMember.getId())) {
			throw new IllegalArgumentException();
		}

		askedFriendHistory.approve();
		makeFriend(askedFriendHistory);
	}

	@Transactional
	public void rejectRequest(String principal, Long requestId) {
		final var  toMember = getMember(principal);
		var askedFriendHistory = friendService.findFriendHistoryById(requestId);

		if (!askedFriendHistory.getToMemberId().equals(toMember.getId())) {
			throw new IllegalArgumentException();
		}

		askedFriendHistory.reject();
	}

	public FriendResponses findFriends(String principal) {
		final var  member = getMember(principal);

		final List<Long> longStream = friendService.findFriends(member.getId())
			.stream()
			.map(Friend::getToMemberId)
			.toList();

		final List<FriendResponse> responseList = memberService.findAllById(longStream)
			.stream()
			.map(m -> new FriendResponse(m.getId(), m.getName(), m.getEmail()))
			.toList();

		return new FriendResponses(responseList);
	}

	public AskedFriendResponses findRequestWandToBeFriend(String principal) {
		final var member = getMember(principal);

		final var waitingAskedFriendHistories = friendService.findWaitingAskedFriendHistories(
			member.getId());

		final var longStream = waitingAskedFriendHistories
			.stream()
			.map(AskedFriendHistory::getFromMemberId)
			.toList();

		final var memberList = memberService.findAllById(longStream);

		final var askedFriendResponses = waitingAskedFriendHistories.stream()
			.map(askedFriendHistory -> {
				Member friend = memberList.stream()
					.filter(m -> m.getId().equals(askedFriendHistory.getFromMemberId()))
					.findFirst()
					.orElseThrow(IllegalArgumentException::new);
				return new AskedFriendResponse(askedFriendHistory.getId(),
					askedFriendHistory.getFromMemberId(), friend.getName(), friend.getEmail());
			}).toList();

		return new AskedFriendResponses(askedFriendResponses);
	}

	private Member getMember(String principal) {
		return memberService.findByEmail(principal);
	}

	private void makeFriend(AskedFriendHistory askedFriendHistory) {
		friendService.saveFriend(new Friend(askedFriendHistory.getFromMemberId(), askedFriendHistory.getToMemberId()));
		friendService.saveFriend(new Friend(askedFriendHistory.getToMemberId(), askedFriendHistory.getFromMemberId()));
	}
}
