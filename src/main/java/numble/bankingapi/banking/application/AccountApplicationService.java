package numble.bankingapi.banking.application;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.alarm.dto.AlarmMessage;
import numble.bankingapi.alarm.dto.TaskStatus;
import numble.bankingapi.alarm.dto.TaskType;
import numble.bankingapi.banking.domain.Account;
import numble.bankingapi.banking.domain.AccountHistory;
import numble.bankingapi.banking.domain.AccountNumber;
import numble.bankingapi.banking.domain.AccountService;
import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.banking.domain.NotifyService;
import numble.bankingapi.banking.dto.HistoryResponse;
import numble.bankingapi.banking.dto.HistoryResponses;
import numble.bankingapi.banking.dto.TargetResponse;
import numble.bankingapi.banking.dto.TargetResponses;
import numble.bankingapi.banking.dto.TransferCommand;
import numble.bankingapi.banking.exception.InvalidMemberException;
import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberService;

@Service
@RequiredArgsConstructor
public class AccountApplicationService {
	private final MemberService memberService;
	private final AccountService accountService;
	private final ConcurrencyFacade concurrencyFacade;
	private final NotifyService notifyService;

	public HistoryResponses getHistory(String principal, String stringAccountNumber) {
		AccountNumber accountNumber = getAccountNumber(stringAccountNumber);
		Account account = accountService.getAccountByAccountNumber(accountNumber);

		Member member = memberService.findByEmail(principal);
		if (!member.getId().equals(account.getUserId())) {
			throw new InvalidMemberException();
		}

		return new HistoryResponses(account.getBalance(),
			accountService.findAccountHistoriesByFromAccountNumber(accountNumber)
				.stream().map(this::getHistoryResponse).collect(Collectors.toList())
		);
	}

	public void deposit(String principal, String number, Money money) {
		AccountNumber accountNumber = getAccountNumber(number);
		Account account = accountService.getAccountByAccountNumber(accountNumber);
		Member member = memberService.findByEmail(principal);
		if (!member.getId().equals(account.getUserId())) {
			throw new InvalidMemberException();
		}

		accountService.depositMoney(accountNumber, money);
		notifyService.notify(member.getId(),
			new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT));
	}

	public void withdraw(String number, Money money) {
		AccountNumber accountNumber = getAccountNumber(number);
		accountService.withdrawMoney(accountNumber, money);
		Account account = accountService.getAccountByAccountNumber(accountNumber);
		notifyService.notify(account.getUserId(),
			new AlarmMessage(TaskStatus.SUCCESS, TaskType.WITHDRAW));
	}

	public void transfer(String accountNumber, TransferCommand command) {
		AccountNumber fromAccountNumber = getAccountNumber(accountNumber);
		AccountNumber toAccountNumber = getAccountNumber(command.toAccountNumber());

		Money money = command.amount();
		concurrencyFacade.transferWithLock(fromAccountNumber, toAccountNumber, money);

		Account fromAccount = accountService.getAccountByAccountNumber(fromAccountNumber);
		Account toAccount = accountService.getAccountByAccountNumber(toAccountNumber);

		notifyService.notify(fromAccount.getUserId(),
			new AlarmMessage(TaskStatus.SUCCESS, TaskType.TRANSFER));
		notifyService.notify(toAccount.getUserId(),
			new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT));
	}

	public TargetResponses getTargets() {
		return new TargetResponses(accountService.findAll()
			.stream().map(this::getTargetResponse)
			.collect(Collectors.toList()));
	}

	private HistoryResponse getHistoryResponse(AccountHistory accountHistory) {
		return new HistoryResponse(accountHistory.getType(), accountHistory.getMoney(),
			accountHistory.getFromAccountNumber(), accountHistory.getToAccountNumber(),
			accountHistory.getCreatedDate());
	}

	private TargetResponse getTargetResponse(Account account) {
		return new TargetResponse(account.getAccountNumber());
	}

	private AccountNumber getAccountNumber(String accountNumber) {
		return new AccountNumber(accountNumber);
	}
}
