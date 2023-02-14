package numble.bankingapi.banking.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountNumber {
	private static final Pattern PATTERN = Pattern.compile("[\\d\\-]+");
	private String number;

	public AccountNumber(String number) {
		validateNumber(number);
		this.number = number;
	}

	private void validateNumber(String number) {
		if (Objects.isNull(number) || number.isBlank()) {
			throw InvalidAccountNumberException.nullAndEmpty();
		}

		if (!PATTERN.matcher(number).matches()) {
			throw InvalidAccountNumberException.invalidString();
		}
	}
}
