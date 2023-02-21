package numble.bankingapi.social.domain;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {

	private final FriendRepository friendRepository;
	private final AskedFriendHistoryRepository askedFriendHistoryRepository;

	public void saveFriend(Friend friend) {
		friendRepository.save(friend);
	}

	public List<Friend> findFriends(Long memberId) {
		return friendRepository.findByFromMemberId(memberId);
	}

	public List<AskedFriendHistory> findWaitingAskedFriendHistories(Long memberId) {
		return askedFriendHistoryRepository.findByToMemberIdAndStatus(memberId, ApprovalStatus.WAITING);
	}

	@Transactional
	public void saveAskedFriendHistory(AskedFriendHistory askedFriendHistory) {
		final var fromMemberId = askedFriendHistory.getFromMemberId();
		final var toMemberId = askedFriendHistory.getToMemberId();
		if (isPresentNotRejectedAskedFriendHistory(fromMemberId, toMemberId)) {
			throw new IllegalArgumentException();
		}

		if (isPresentNotRejectedAskedFriendHistory(toMemberId, fromMemberId)) {
			throw new IllegalArgumentException();
		}

		askedFriendHistoryRepository.save(askedFriendHistory);
	}

	private boolean isPresentNotRejectedAskedFriendHistory(Long fromMember, Long toMember) {
		return askedFriendHistoryRepository
			.findByFromMemberIdAndToMemberId(fromMember, toMember)
			.filter(foundAskedFriendHistory -> !ApprovalStatus.REJECTED.equals(
				foundAskedFriendHistory.getStatus())).isPresent();
	}

	public void approvedAskedFriend(Long askedFriendHistoryId) {
		AskedFriendHistory askedFriendHistory = askedFriendHistoryRepository.findById(askedFriendHistoryId)
			.orElseThrow(IllegalArgumentException::new);
		askedFriendHistory.approve();
	}

	public void rejectAskedFriend(Long askedFriendHistoryId) {
		AskedFriendHistory askedFriendHistory = askedFriendHistoryRepository.findById(askedFriendHistoryId)
			.orElseThrow(IllegalArgumentException::new);
		askedFriendHistory.reject();
	}
}
