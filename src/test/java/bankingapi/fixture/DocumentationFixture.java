package bankingapi.fixture;

import java.time.LocalDateTime;
import java.util.List;

import bankingapi.banking.domain.AccountNumber;
import bankingapi.banking.domain.HistoryType;
import bankingapi.banking.dto.HistoryResponse;
import bankingapi.banking.dto.HistoryResponses;
import bankingapi.banking.dto.TargetResponse;
import bankingapi.banking.dto.TargetResponses;
import bankingapi.util.generator.AccountNumberGenerator;

public class DocumentationFixture {

	public static final String 계좌_번호 = AccountNumberGenerator.generate().getNumber();
	public static final HistoryResponses 계좌_내역 = new HistoryResponses(
		AccountFixture.이만원,
		List.of(
			new HistoryResponse(HistoryType.DEPOSIT, AccountFixture.이만원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 2, 13, 12, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, AccountFixture.오천원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 2, 3, 4, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, AccountFixture.만원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 2, 1, 21, 10)),
			new HistoryResponse(HistoryType.DEPOSIT, AccountFixture.십만원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 1, 21, 20, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, AccountFixture.만오천원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 1, 10, 8, 10))
		));

	public static final TargetResponses 타겟목록 = new TargetResponses(List.of(
		new TargetResponse("name1", "member1@email.com", AccountNumberGenerator.generate()),
		new TargetResponse("name2", "member2@email.com", AccountNumberGenerator.generate()),
		new TargetResponse("name3", "member3@email.com", AccountNumberGenerator.generate())
	));
}
