package numble.bankingapi.social.domain;

import java.util.List;

public interface FriendRepository {
	<S extends Friend> S save(S entity);

	List<Friend> findByFromMemberId(Long memberId);

}
