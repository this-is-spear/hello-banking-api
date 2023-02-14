package numble.bankingapi.banking.dto;

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
