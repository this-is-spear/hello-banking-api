package numble.bankingapi.banking.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {
	@EqualsAndHashCode.Include
	private Long id;
	private Long userId;
	private AccountNumber accountNumber;
	private Money balance;
	private LocalDateTime createdDate;

	@Builder
	public Account(Long userId, AccountNumber accountNumber, Money balance, LocalDateTime createdDate) {
		Objects.requireNonNull(userId);
		Objects.requireNonNull(accountNumber);
		Objects.requireNonNull(balance);

		this.userId = userId;
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.createdDate = createdDate == null ? LocalDateTime.now() : createdDate;
	}

	public void deposit(Money money) {
		this.balance = balance.plus(money);
	}

	public void withdraw(Money money) {
		this.balance = balance.minus(money);
	}
}
