package bankingapi.banking.ui;

import bankingapi.banking.domain.AccountNumber;
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
import bankingapi.banking.application.AccountApplicationService;
import bankingapi.banking.domain.Money;
import bankingapi.banking.dto.HistoryResponses;
import bankingapi.banking.dto.TargetResponses;
import bankingapi.banking.dto.TransferCommand;

import java.util.List;

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
		value = "/transfer/targets",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<TargetResponses> getTargets(@AuthenticationPrincipal UserDetails principal) {
		return ResponseEntity.ok(accountApplicationService.getTargets(principal.getUsername()));
	}

	@GetMapping(
		value = "/targets",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<List<AccountNumber>> findAccounts(@AuthenticationPrincipal UserDetails principal) {
		return ResponseEntity.ok(accountApplicationService.findAccounts(principal.getUsername()));
	}
}
