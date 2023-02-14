package numble.bankingapi.banking.domain;

import java.util.Objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import numble.bankingapi.common.BaseEntity;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AccountHistory extends BaseEntity {
	@EqualsAndHashCode.Include
	private Long id;
	private AccountNumber fromAccountNumber;
	private AccountNumber toAccountNumber;
	private HistoryType type;
	private Money money;

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
