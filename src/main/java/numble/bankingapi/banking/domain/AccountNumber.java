package numble.bankingapi.banking.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import numble.bankingapi.banking.exception.InvalidAccountNumberException;

@Getter
@ToString
@Embeddable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountNumber {
	private static final Pattern PATTERN = Pattern.compile("[\\d\\-]+");
	@EqualsAndHashCode.Include
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
