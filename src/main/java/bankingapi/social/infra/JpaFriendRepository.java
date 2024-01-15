package bankingapi.social.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bankingapi.social.domain.Friend;
import bankingapi.social.domain.FriendRepository;

@Repository
public interface JpaFriendRepository extends JpaRepository<Friend, Long>, FriendRepository {
	@Override
	<S extends Friend> S save(S entity);

	List<Friend> findByFromMemberId(Long memberId);
}
