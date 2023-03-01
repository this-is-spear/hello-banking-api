package numble.bankingapi.member.domain;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
	<S extends Member> S save(S entity);

	Optional<Member> findByEmail(String email);

	Optional<Member> findById(Long aLong);

	List<Member> findAllById(Iterable<Long> longs);
}
