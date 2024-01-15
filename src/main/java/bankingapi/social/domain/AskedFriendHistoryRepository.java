package bankingapi.social.domain;

import java.util.List;
import java.util.Optional;

public interface AskedFriendHistoryRepository {
	<S extends AskedFriendHistory> S save(S entity);

	Optional<AskedFriendHistory> findById(Long aLong);

	Optional<AskedFriendHistory> findByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);

	List<AskedFriendHistory> findByToMemberIdAndStatus(Long toMemberId, ApprovalStatus status);
}
