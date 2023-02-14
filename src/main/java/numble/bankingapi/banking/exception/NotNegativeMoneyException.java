package numble.bankingapi.banking.exception;

public class NotNegativeMoneyException extends RuntimeException {
	public NotNegativeMoneyException(String message) {
		super(message);
	}
}
