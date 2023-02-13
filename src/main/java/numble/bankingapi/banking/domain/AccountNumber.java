package numble.bankingapi.banking.domain;

import java.util.regex.Pattern;

public class AccountNumber {
	private static final Pattern PATTERN = Pattern.compile("[\\d\\-]+");
	private String number;

	private AccountNumber() {/*no-op*/}

	public AccountNumber(String number) {
		validateNumber(number);
		this.number = number;
	}

	public String getNumber() {
		return number;
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
