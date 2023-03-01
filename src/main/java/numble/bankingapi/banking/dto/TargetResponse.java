package numble.bankingapi.banking.dto;

import numble.bankingapi.banking.domain.AccountNumber;

public record TargetResponse(
	String name,
	String email,
	AccountNumber accountNumber
) {
}
