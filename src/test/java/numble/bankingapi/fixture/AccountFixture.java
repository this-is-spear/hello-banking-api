package numble.bankingapi.fixture;

import numble.bankingapi.banking.domain.Account;
import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.HistoryType;
import numble.bankingapi.banking.domain.Money;

public class AccountFixture {
	public static final AccountNumber 계좌번호 = new AccountNumber("123-234-2");
	public static final AccountNumber 상대방_계좌번호 = new AccountNumber("34-4353-1322");
	public static final Money 삼만원 = new Money(30_000L);
	public static final Money 이만원 = new Money(20_000);
	public static final Money 만원 = new Money(10_000L);
	public static final long 사용자_ID = 3L;
	public static final HistoryType 입금 = HistoryType.DEPOSIT;
}
