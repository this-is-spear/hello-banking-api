package numble.bankingapi.member.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberRepository;

@Repository
public interface JpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {
	@Override
	<S extends Member> S save(S entity);

	@Override
	Optional<Member> findByEmail(String email);

	@Override
	Optional<Member> findById(Long aLong);

	@Override
	List<Member> findAllById(Iterable<Long> longs);
}
