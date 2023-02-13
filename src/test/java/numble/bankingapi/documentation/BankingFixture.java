package numble.bankingapi.documentation;

import java.time.LocalDateTime;
import java.util.List;

import numble.bankingapi.banking.application.TargetResponse;
import numble.bankingapi.banking.application.TargetResponses;
import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.HistoryType;
import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.banking.ui.HistoryResponse;
import numble.bankingapi.banking.ui.HistoryResponses;

public class BankingFixture {

	public static final Money 오천원 = new Money(5_000);
	public static final Money 만원 = new Money(10_000);
	public static final Money 만오천원 = new Money(15_000);
	public static final Money 이만원 = new Money(20_000);
	public static final Money 십만원 = new Money(100_000);
	public static final String 계좌_번호 = "123-233-242-12";
	public static final HistoryResponses 계좌_내역 = new HistoryResponses(
		List.of(
			new HistoryResponse(HistoryType.DEPOSIT, 이만원, new AccountNumber(계좌_번호),
				new AccountNumber("123-4324-123-12"), LocalDateTime.of(2022, 2, 13, 12, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, 오천원, new AccountNumber(계좌_번호),
				new AccountNumber("123-321213-1231"), LocalDateTime.of(2022, 2, 3, 4, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, 만원, new AccountNumber(계좌_번호),
				new AccountNumber("333-324234-123-2"), LocalDateTime.of(2022, 2, 1, 21, 10)),
			new HistoryResponse(HistoryType.DEPOSIT, 십만원, new AccountNumber(계좌_번호),
				new AccountNumber("342-3243-2323-2"), LocalDateTime.of(2022, 1, 21, 20, 10)),
			new HistoryResponse(HistoryType.WITHDRAW, 만오천원, new AccountNumber(계좌_번호),
				new AccountNumber("1231240-234-324"), LocalDateTime.of(2022, 1, 10, 8, 10))
		));

	public static final TargetResponses 타겟목록 = new TargetResponses(List.of(
		new TargetResponse("this-is-spear", "123-755-1212"),
		new TargetResponse("this-is-water", "3333-2-213-5463"),
		new TargetResponse("this-is-moi", "213-454-6248")
	));
}
