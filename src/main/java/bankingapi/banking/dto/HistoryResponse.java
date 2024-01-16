package bankingapi.banking.dto;

import java.time.LocalDateTime;

import bankingapi.banking.domain.AccountNumber;
import bankingapi.banking.domain.HistoryType;
import bankingapi.banking.domain.Money;

public record HistoryResponse(
	HistoryType historyType,
	Money money,
	AccountNumber fromAccountNumber,
	AccountNumber toAccountNumber,
	LocalDateTime recordDate
) {
}
