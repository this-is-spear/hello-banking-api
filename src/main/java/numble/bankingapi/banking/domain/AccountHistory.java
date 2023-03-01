package numble.bankingapi.banking.domain;

import java.util.Objects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import numble.bankingapi.common.BaseEntity;

@Getter
@Entity
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AccountHistory extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ToString.Include
	@EqualsAndHashCode.Include
	private Long id;
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "number", column = @Column(name = "fromAccountNumber"))
	})
	private AccountNumber fromAccountNumber;
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "number", column = @Column(name = "toAccountNumber"))
	})
	private AccountNumber toAccountNumber;
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "amount", column = @Column(name = "money"))
	})
	private Money money;
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "amount", column = @Column(name = "balance"))
	})
	private Money balance;
	@Enumerated(EnumType.STRING)
	private HistoryType type;

	public AccountHistory(Long id, AccountNumber fromAccountNumber, AccountNumber toAccountNumber, Money money,
		Money balance, HistoryType type) {
		Objects.requireNonNull(fromAccountNumber);
		Objects.requireNonNull(toAccountNumber);
		Objects.requireNonNull(type);
		Objects.requireNonNull(money);
		Objects.requireNonNull(balance);

		this.id = id;
		this.fromAccountNumber = fromAccountNumber;
		this.toAccountNumber = toAccountNumber;
		this.money = money;
		this.balance = balance;
		this.type = type;
	}

	@Builder
	public AccountHistory(AccountNumber fromAccountNumber, AccountNumber toAccountNumber, HistoryType type,
		Money money, Money balance) {
		this(null, fromAccountNumber, toAccountNumber, balance, money, type);
	}
}
