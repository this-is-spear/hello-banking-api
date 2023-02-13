package numble.bankingapi.banking.ui;

import java.time.LocalDateTime;

import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.HistoryType;
import numble.bankingapi.banking.domain.Money;

public record HistoryResponse(
	HistoryType historyType,
	Money money,
	AccountNumber fromAccountNumber,
	AccountNumber toAccountNumber,
	LocalDateTime recordDate
) {
}
