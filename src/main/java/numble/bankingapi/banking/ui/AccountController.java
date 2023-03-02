package numble.bankingapi.banking.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.banking.application.AccountApplicationService;
import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.banking.dto.HistoryResponses;
import numble.bankingapi.banking.dto.TargetResponses;
import numble.bankingapi.banking.dto.TransferCommand;

@RestController
@RequestMapping("account")
@RequiredArgsConstructor
public class AccountController {

	private final AccountApplicationService accountApplicationService;

	@GetMapping(
		value = "/{accountNumber}/history",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<HistoryResponses> getHistory(@AuthenticationPrincipal UserDetails principal,
		@PathVariable String accountNumber) {
		return ResponseEntity.ok(accountApplicationService.getHistory(principal.getUsername(), accountNumber));
	}

	@PostMapping(
		value = "/{accountNumber}/deposit",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Void> depositMoney(@AuthenticationPrincipal UserDetails principal,
		@PathVariable String accountNumber, @RequestBody Money money) {
		accountApplicationService.deposit(principal.getUsername(), accountNumber, money);
		return ResponseEntity.ok().build();
	}

	@PostMapping(
		value = "/{accountNumber}/withdraw",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Void> withdrawMoney(@AuthenticationPrincipal UserDetails principal,
		@PathVariable String accountNumber, @RequestBody Money money) {
		accountApplicationService.withdraw(principal.getUsername(), accountNumber, money);
		return ResponseEntity.ok().build();
	}

	@PostMapping(
		value = "/{accountNumber}/transfer",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Void> transferMoney(@AuthenticationPrincipal UserDetails principal,
		@PathVariable String accountNumber,
		@RequestBody TransferCommand transferCommand) {
		accountApplicationService.transfer(principal.getUsername(), accountNumber, transferCommand);
		return ResponseEntity.ok().build();
	}

	@GetMapping(
		value = "/{accountNumber}/transfer/targets",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<TargetResponses> getTargets(@AuthenticationPrincipal UserDetails principal,
		@PathVariable String accountNumber) {
		return ResponseEntity.ok(accountApplicationService.getTargets(principal.getUsername(), accountNumber));
	}
}
