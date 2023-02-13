package numble.bankingapi.banking.application;

import org.springframework.stereotype.Service;

import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.banking.ui.HistoryResponses;
import numble.bankingapi.banking.ui.TransferCommand;

@Service
public class AccountApplicationService {
	public HistoryResponses getHistory(String accountNumber) {
		return null;
	}

	public void deposit(String accountNumber, Money money) {
	}

	public void withdraw(String accountNumber, Money money) {
	}

	public void transfer(String accountNumber, TransferCommand money) {
	}

	public TargetResponses getTargets() {
		return null;
	}
}
