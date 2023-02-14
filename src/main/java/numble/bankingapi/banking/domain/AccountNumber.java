package numble.bankingapi.banking.domain;

import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountNumber {
	private static final Pattern PATTERN = Pattern.compile("[\\d\\-]+");
	private String number;

	public AccountNumber(String number) {
		validateNumber(number);
		this.number = number;
	}

	private void validateNumber(String number) {
		if (number == null || number.isBlank()) {
			throw InvalidAccountNumberException.nullAndEmpty();
		}

		if (!PATTERN.matcher(number).matches()) {
			throw InvalidAccountNumberException.invalidString();
		}
	}
}
