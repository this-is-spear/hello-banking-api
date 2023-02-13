package numble.bankingapi.banking.domain;

public class AccountNumber {
	private String number;

	private AccountNumber() {}

	public AccountNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}
}
