package numble.bankingapi.banking.domain;

public class Money {
	private long amount;

	private Money() {
	}

	public Money(long amount) {
		this.amount = amount;
	}

	public long getAmount() {
		return amount;
	}
}
