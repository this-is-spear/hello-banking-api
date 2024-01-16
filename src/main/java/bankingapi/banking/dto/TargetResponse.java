package bankingapi.banking.dto;

import bankingapi.banking.domain.AccountNumber;

public record TargetResponse(
	String name,
	String email,
	AccountNumber accountNumber
) {
}
