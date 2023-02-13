package numble.bankingapi.banking.domain;

public class NotNegativeMoneyException extends RuntimeException{
	public NotNegativeMoneyException(String message) {
		super(message);
	}
}
