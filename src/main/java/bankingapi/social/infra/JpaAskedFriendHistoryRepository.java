package bankingapi.social.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bankingapi.social.domain.ApprovalStatus;
import bankingapi.social.domain.AskedFriendHistory;
import bankingapi.social.domain.AskedFriendHistoryRepository;

@Repository
public interface JpaAskedFriendHistoryRepository
	extends JpaRepository<AskedFriendHistory, Long>, AskedFriendHistoryRepository {
	<S extends AskedFriendHistory> S save(S entity);

	@Override
	Optional<AskedFriendHistory> findById(Long aLong);

	Optional<AskedFriendHistory> findByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);

	List<AskedFriendHistory> findByToMemberIdAndStatus(Long toMemberId, ApprovalStatus status);
}
