package numble.bankingapi.banking.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "Account")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	@EqualsAndHashCode.Include
	private Long id;
	@Column(name = "userId", nullable = false)
	private Long userId;
	@Embedded
	@Column(nullable = false, unique = true)
	@AttributeOverrides({
		@AttributeOverride(name = "number", column = @Column(name = "accountNumber"))
	})
	private AccountNumber accountNumber;
	@Embedded
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "amount", column = @Column(name = "balance"))
	})
	private Money balance;
	@Column(name = "createdDate", nullable = false)
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
