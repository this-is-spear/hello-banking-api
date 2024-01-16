package bankingapi.banking.domain;

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
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import bankingapi.common.BaseEntity;

@Getter
@Entity
@Table(name = "Account")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Account extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	@ToString.Include
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
	@Version
	private long version;

	public Account(Long id, Long userId, AccountNumber accountNumber, Money balance) {
		Objects.requireNonNull(userId);
		Objects.requireNonNull(accountNumber);
		Objects.requireNonNull(balance);

		this.id = id;
		this.userId = userId;
		this.accountNumber = accountNumber;
		this.balance = balance;
	}

	@Builder
	public Account(Long userId, AccountNumber accountNumber, Money balance) {
		this(null, userId, accountNumber, balance);
	}

	public void deposit(Money money) {
		this.balance = balance.plus(money);
	}

	public void withdraw(Money money) {
		this.balance = balance.minus(money);
	}
}

