package bankingapi.fixture;

import bankingapi.banking.domain.AccountNumber;
import bankingapi.banking.domain.HistoryType;
import bankingapi.banking.domain.Money;
import bankingapi.util.generator.AccountNumberGenerator;

public class AccountFixture {
	public static final AccountNumber 계좌번호 = AccountNumberGenerator.generate();
	public static final AccountNumber 상대방_계좌번호 = AccountNumberGenerator.generate();
	public static final AccountNumber 자신의_계좌반호 = AccountNumberGenerator.generate();
	public static final Money 오천원 = new Money(5_000);
	public static final Money 만원 = new Money(10_000);
	public static final Money 만오천원 = new Money(15_000);
	public static final Money 이만원 = new Money(20_000);
	public static final Money 삼만원 = new Money(30_000L);
	public static final Money 십만원 = new Money(100_000);
	public static final long 사용자_ID = 3L;
	public static final HistoryType 입금 = HistoryType.DEPOSIT;
}
