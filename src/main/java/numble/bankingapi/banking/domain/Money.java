package numble.bankingapi.banking.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Money {
	private static final int ZERO = 0;
	private long amount;

	public Money(long amount) {
		validateAmount(amount);
		this.amount = amount;
	}

	public static Money zero() {
		return new Money(ZERO);
	}

	private void validateAmount(long amount) {
		if (amount < 0) {
			throw new NotNegativeMoneyException("금액은 0원 이상이어야 합니다.");
		}
	}

	public Money minus(Money money) {
		return new Money(this.amount - money.getAmount());
	}

	public Money plus(Money money) {
		return new Money(this.amount + money.amount);
	}
}
