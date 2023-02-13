package numble.bankingapi.banking.domain;

public class Money {
	private long amount;

	private Money() {/*no-op*/}

	public Money(long amount) {
		validateAmount(amount);
		this.amount = amount;
	}

	public long getAmount() {
		return amount;
	}

	private void validateAmount(long amount) {
		if (amount < 0) {
			throw new NotNegativeException("금액은 0원 이상이어야 합니다.");
		}
	}
}
