package bankingapi.banking.domain;

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
import bankingapi.common.BaseEntity;

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
	@ToString.Include
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "number", column = @Column(name = "fromAccountNumber"))
	})
	private AccountNumber fromAccountNumber;
	@ToString.Include
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "number", column = @Column(name = "toAccountNumber"))
	})
	private AccountNumber toAccountNumber;
	@ToString.Include
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "amount", column = @Column(name = "money"))
	})
	private Money money;
	@ToString.Include
	@Column(nullable = false)
	@AttributeOverrides({
		@AttributeOverride(name = "amount", column = @Column(name = "balance"))
	})
	private Money balance;
	@ToString.Include
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
		this(null, fromAccountNumber, toAccountNumber, money, balance, type);
	}

	public static AccountHistory recordWithdrawHistory(Account fromAccount, Account toAccount, Money money) {
		return new AccountHistory(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), HistoryType.WITHDRAW,
			money, fromAccount.getBalance());
	}

	public static AccountHistory recordDepositHistory(Account fromAccount, Account toAccount, Money money) {
		return new AccountHistory(fromAccount.getAccountNumber(), toAccount.getAccountNumber(), HistoryType.DEPOSIT,
			money, fromAccount.getBalance());
	}
}
