package numble.bankingapi.banking.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Money {
	private long amount;

	public Money(long amount) {
		validateAmount(amount);
		this.amount = amount;
	}

	private void validateAmount(long amount) {
		if (amount < 0) {
			throw new NotNegativeMoneyException("금액은 0원 이상이어야 합니다.");
		}
	}
}
