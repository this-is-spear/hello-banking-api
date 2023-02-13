package numble.bankingapi.banking.domain;

public class Money {
	private long amount;

	private Money() {
	}

	public Money(long amount) {
		if (amount < 0) {
			throw new NotNegativeException("금액은 0원 이상이어야 합니다.");
		}
		this.amount = amount;
	}

	public long getAmount() {
		return amount;
	}
}
