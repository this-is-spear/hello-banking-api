package numble.bankingapi.banking.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import numble.bankingapi.banking.domain.Money;

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record TransferCommand(
	String toAccountNumber,
	Money money
) {
}
