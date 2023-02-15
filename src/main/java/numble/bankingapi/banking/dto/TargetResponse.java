package numble.bankingapi.banking.dto;

import numble.bankingapi.banking.domain.AccountNumber;

public record TargetResponse(
	AccountNumber accountNumber
) {
}
