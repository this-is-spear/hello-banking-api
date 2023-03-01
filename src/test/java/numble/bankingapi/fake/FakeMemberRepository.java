package numble.bankingapi.fake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberRepository;

public class FakeMemberRepository implements MemberRepository {

	Map<Long, Member> maps = new HashMap<>();
	private Long sequence = 0L;

	@Override
	public Member save(Member entity) {
		var id = ++sequence;
		var member = new Member(id, entity.getEmail(), entity.getName(), entity.getPassword(), entity.getRoles());
		maps.put(id, member);
		return member;
	}

	@Override
	public Optional<Member> findByEmail(String email) {
		return maps.values().stream().filter(member -> member.getEmail().equals(email)).findFirst();
	}

	@Override
	public Optional<Member> findById(Long aLong) {
		return Optional.ofNullable(maps.get(aLong));
	}

	@Override
	public List<Member> findAllById(Iterable<Long> longs) {
		List<Member> members = new ArrayList<>();
		longs.iterator().forEachRemaining(aLong -> {
			if (maps.containsKey(aLong)) {
				members.add(maps.get(aLong));
			}
		});
		return members;
	}
}
