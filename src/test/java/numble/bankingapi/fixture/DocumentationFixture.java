package numble.bankingapi.fixture;

import static numble.bankingapi.fixture.AccountFixture.*;

import java.time.LocalDateTime;
import java.util.List;

import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.HistoryType;
import numble.bankingapi.banking.dto.HistoryResponse;
import numble.bankingapi.banking.dto.HistoryResponses;
import numble.bankingapi.banking.dto.TargetResponse;
import numble.bankingapi.banking.dto.TargetResponses;
import numble.bankingapi.util.AccountNumberGenerator;

public class DocumentationFixture {

	public static final String 계좌_번호 = AccountNumberGenerator.generate().getNumber();
	public static final HistoryResponses 계좌_내역 = new HistoryResponses(
		이만원,
		List.of(
			new HistoryResponse(HistoryType.DEPOSIT, 이만원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 2, 13, 12, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, 오천원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 2, 3, 4, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, 만원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 2, 1, 21, 10)),
			new HistoryResponse(HistoryType.DEPOSIT, 십만원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 1, 21, 20, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, 만오천원, new AccountNumber(계좌_번호),
				AccountNumberGenerator.generate(), LocalDateTime.of(2022, 1, 10, 8, 10))
		));

	public static final TargetResponses 타겟목록 = new TargetResponses(List.of(
		new TargetResponse(AccountNumberGenerator.generate()),
		new TargetResponse(AccountNumberGenerator.generate()),
		new TargetResponse(AccountNumberGenerator.generate())
	));
}
