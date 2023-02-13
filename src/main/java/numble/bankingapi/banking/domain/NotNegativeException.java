package numble.bankingapi.banking.domain;

public class NotNegativeException extends RuntimeException{
	public NotNegativeException(String message) {
		super(message);
	}
}
