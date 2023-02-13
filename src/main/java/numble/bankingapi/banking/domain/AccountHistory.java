package numble.bankingapi.banking.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccountHistory {
	@EqualsAndHashCode.Include
	private Long id;
	private AccountNumber fromAccountNumber;
	private AccountNumber toAccountNumber;
	private HistoryType type;
	private Money money;
	private LocalDateTime recordedDate;

	@Builder
	public AccountHistory(AccountNumber fromAccountNumber, AccountNumber toAccountNumber, HistoryType type, Money money,
		LocalDateTime recordedDate) {
		Objects.requireNonNull(fromAccountNumber);
		Objects.requireNonNull(toAccountNumber);
		Objects.requireNonNull(type);
		Objects.requireNonNull(money);
		this.fromAccountNumber = fromAccountNumber;
		this.toAccountNumber = toAccountNumber;
		this.type = type;
		this.money = money;
		this.recordedDate = recordedDate == null ? LocalDateTime.now() : recordedDate;
	}
}
