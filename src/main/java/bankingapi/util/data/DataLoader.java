package bankingapi.util.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bankingapi.member.domain.Member;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import bankingapi.banking.domain.Account;
import bankingapi.banking.domain.AccountRepository;
import bankingapi.banking.domain.Money;
import bankingapi.member.domain.MemberRepository;
import bankingapi.member.domain.RoleType;
import bankingapi.util.generator.AccountNumberGenerator;

@Component
@RequiredArgsConstructor
public class DataLoader {
	private final MemberRepository memberRepository;
	private final AccountRepository accountRepository;

	public Map<String, String> loadData() {
		Member admin = memberRepository.save(
			new Member("admin@email.com", "admin", "password", List.of(RoleType.ROLE_ADMIN.name())));
		Account adminAccount = accountRepository.save(
			new Account(admin.getId(), AccountNumberGenerator.generate(), Money.zero()));

		Member member = memberRepository.save(
			new Member("member@email.com", "member", "password", List.of(RoleType.ROLE_MEMBER.name())));
		Account memberAccount = accountRepository.save(
			new Account(member.getId(), AccountNumberGenerator.generate(), Money.zero()));

		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("admin", adminAccount.getAccountNumber().getNumber());
		hashMap.put("member", memberAccount.getAccountNumber().getNumber());
		hashMap.put("adminId", String.valueOf(admin.getId()));
		hashMap.put("memberId", String.valueOf(member.getId()));
		return hashMap;
	}
}
