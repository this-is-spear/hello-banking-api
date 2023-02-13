package numble.bankingapi.banking.domain;

public class InvalidAccountNumberException extends RuntimeException {
	public InvalidAccountNumberException(String message) {
		super(message);
	}

	public static InvalidAccountNumberException nullAndEmpty() {
		return new InvalidAccountNumberException("비어있을 수 없습니다.");
	}

	public static InvalidAccountNumberException invalidString() {
		return new InvalidAccountNumberException("유효하지 않은 문자가 포함되어 있습니다.");
	}
}
