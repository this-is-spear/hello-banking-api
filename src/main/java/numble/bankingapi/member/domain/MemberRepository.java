package numble.bankingapi.member.domain;

import java.util.Optional;

public interface MemberRepository {
	<S extends Member> S save(S entity);

	Optional<Member> findByEmail(String email);
}
