package numble.bankingapi.social.domain;

import java.util.List;

import org.springframework.stereotype.Service;

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

	public void askWantToBefriends(String principal, Long toMemberId) {
		Member fromMember = getMember(principal);
		Member toMember = memberService.findById(toMemberId);
		AskedFriendHistory waitingAskedFriend = new AskedFriendHistory(fromMember.getId(), toMember.getId());
		friendService.saveAskedFriendHistory(waitingAskedFriend);
	}

	public void approvalRequest(String principal, Long requestId) {
		Member toMember = getMember(principal);
		AskedFriendHistory askedFriendHistory = friendService.findFriendHistoryById(requestId);

		if (!askedFriendHistory.getToMemberId().equals(toMember.getId())) {
			throw new IllegalArgumentException();
		}

		askedFriendHistory.approve();
		makeFriend(askedFriendHistory);
	}

	public void rejectRequest(String principal, Long requestId) {
		Member toMember = getMember(principal);
		AskedFriendHistory askedFriendHistory = friendService.findFriendHistoryById(requestId);

		if (!askedFriendHistory.getToMemberId().equals(toMember.getId())) {
			throw new IllegalArgumentException();
		}

		askedFriendHistory.reject();
	}

	public FriendResponses findFriends(String principal) {
		Member member = getMember(principal);

		List<Long> longStream = friendService.findFriends(member.getId())
			.stream()
			.map(Friend::getToMemberId)
			.toList();

		List<FriendResponse> responseList = memberService.findAllById(longStream)
			.stream()
			.map(m -> new FriendResponse(m.getId(), m.getName(), m.getEmail()))
			.toList();

		return new FriendResponses(responseList);
	}

	public AskedFriendResponses findRequestWandToBeFriend(String principal) {
		Member member = getMember(principal);

		List<AskedFriendHistory> waitingAskedFriendHistories = friendService.findWaitingAskedFriendHistories(
			member.getId());

		List<Long> longStream = waitingAskedFriendHistories
			.stream()
			.map(AskedFriendHistory::getFromMemberId)
			.toList();

		List<Member> memberList = memberService.findAllById(longStream);

		List<AskedFriendResponse> askedFriendResponses = waitingAskedFriendHistories.stream()
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
