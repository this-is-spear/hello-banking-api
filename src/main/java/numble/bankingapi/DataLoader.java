package numble.bankingapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.banking.domain.Account;
import numble.bankingapi.banking.domain.AccountRepository;
import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberRepository;
import numble.bankingapi.member.domain.RoleType;
import numble.bankingapi.util.AccountNumberGenerator;

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
