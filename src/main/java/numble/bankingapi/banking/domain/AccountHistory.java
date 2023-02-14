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
import numble.bankingapi.common.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AccountHistory extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	@AttributeOverrides({
		@AttributeOverride(name = "number", column = @Column(name = "fromAccountNumber"))
	})
	private AccountNumber fromAccountNumber;
	@AttributeOverrides({
		@AttributeOverride(name = "number", column = @Column(name = "toAccountNumber"))
	})
	private AccountNumber toAccountNumber;
	@AttributeOverrides({
		@AttributeOverride(name = "amount", column = @Column(name = "money"))
	})
	private Money money;
	@Enumerated(EnumType.STRING)
	private HistoryType type;

	@Builder
	public AccountHistory(AccountNumber fromAccountNumber, AccountNumber toAccountNumber, HistoryType type,
		Money money) {
		Objects.requireNonNull(fromAccountNumber);
		Objects.requireNonNull(toAccountNumber);
		Objects.requireNonNull(type);
		Objects.requireNonNull(money);
		this.fromAccountNumber = fromAccountNumber;
		this.toAccountNumber = toAccountNumber;
		this.type = type;
		this.money = money;
	}
}
