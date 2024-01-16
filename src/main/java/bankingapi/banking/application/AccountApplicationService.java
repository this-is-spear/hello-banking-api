package bankingapi.banking.application;

import java.util.List;
import java.util.stream.Collectors;

import bankingapi.alarm.dto.AlarmMessage;
import bankingapi.alarm.dto.TaskStatus;
import bankingapi.alarm.dto.TaskType;
import bankingapi.banking.exception.InvalidMemberException;
import bankingapi.member.domain.Member;
import bankingapi.member.domain.MemberService;
import bankingapi.social.domain.Friend;
import bankingapi.social.domain.FriendService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import bankingapi.banking.domain.Account;
import bankingapi.banking.domain.AccountHistory;
import bankingapi.banking.domain.AccountNumber;
import bankingapi.banking.domain.AccountService;
import bankingapi.banking.domain.Money;
import bankingapi.banking.domain.NotifyService;
import bankingapi.banking.dto.HistoryResponse;
import bankingapi.banking.dto.HistoryResponses;
import bankingapi.banking.dto.TargetResponse;
import bankingapi.banking.dto.TargetResponses;
import bankingapi.banking.dto.TransferCommand;

@Service
@RequiredArgsConstructor
public class AccountApplicationService {
	private final MemberService memberService;
	private final FriendService friendService;
	private final AccountService accountService;
	private final NotifyService notifyService;
	private final ConcurrencyFacade concurrencyFacade;

	public HistoryResponses getHistory(String principal, String stringAccountNumber) {
		final var accountNumber = getAccountNumber(stringAccountNumber);
		final var account = accountService.getAccountByAccountNumber(accountNumber);
		validateMember(principal, account);

		return new HistoryResponses(account.getBalance(),
			accountService.findAccountHistoriesByFromAccountNumber(account)
				.stream().map(this::getHistoryResponse).collect(Collectors.toList())
		);
	}

	public void deposit(String principal, String number, Money money) {
		final var accountNumber = getAccountNumber(number);
		final var account = accountService.getAccountByAccountNumber(accountNumber);
		validateMember(principal, account);

		concurrencyFacade.depositWithLock(accountNumber, money);
		notifyService.notify(account.getUserId(),
			new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT));
	}

	public void withdraw(String principal, String number, Money money) {
		final var accountNumber = getAccountNumber(number);
		final var account = accountService.getAccountByAccountNumber(accountNumber);
		validateMember(principal, account);

		concurrencyFacade.withdrawWithLock(accountNumber, money);
		notifyService.notify(account.getUserId(),
			new AlarmMessage(TaskStatus.SUCCESS, TaskType.WITHDRAW));
	}

	public void transfer(String principal, String accountNumber, TransferCommand command) {
		final var fromAccountNumber = getAccountNumber(accountNumber);
		final var toAccountNumber = getAccountNumber(command.toAccountNumber());

		final var fromAccount = accountService.getAccountByAccountNumber(fromAccountNumber);
		final var toAccount = accountService.getAccountByAccountNumber(toAccountNumber);

		validateMember(principal, fromAccount);
		Money money = command.amount();

		concurrencyFacade.transferWithLock(fromAccountNumber, toAccountNumber, money);

		notifyService.notify(fromAccount.getUserId(),
			new AlarmMessage(TaskStatus.SUCCESS, TaskType.TRANSFER));
		notifyService.notify(toAccount.getUserId(),
			new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT));
	}

	private void validateMember(String principal, Account account) {
		final var member = memberService.findByEmail(principal);

		if (!member.getId().equals(account.getUserId())) {
			throw new InvalidMemberException();
		}
	}

	public TargetResponses getTargets(String principal, String stringAccountNumber) {
		final var accountNumber = new AccountNumber(stringAccountNumber);
		final var account = accountService.getAccountByAccountNumber(accountNumber);

		final var member = memberService.findByEmail(principal);
		if (!member.getId().equals(account.getUserId())) {
			throw new InvalidMemberException();
		}

		final var friendIds = friendService.findFriends(member.getId())
			.stream()
			.map(Friend::getToMemberId)
			.toList();

		final var targetList = memberService.findAllById(friendIds);
		final var targetResponseList = accountService.getFriendAccounts(friendIds)
			.stream()
			.map(friendAccount -> {
				Member friend = targetList.stream()
					.filter(target -> target.getId().equals(friendAccount.getUserId()))
					.findFirst()
					.orElseThrow(IllegalArgumentException::new);
				return new TargetResponse(friend.getName(), friend.getEmail(), friendAccount.getAccountNumber());
			}).collect(Collectors.toList());

		return new TargetResponses(targetResponseList);
	}

	private HistoryResponse getHistoryResponse(AccountHistory accountHistory) {
		return new HistoryResponse(accountHistory.getType(), accountHistory.getMoney(),
			accountHistory.getFromAccountNumber(), accountHistory.getToAccountNumber(),
			accountHistory.getCreatedDate());
	}

	private AccountNumber getAccountNumber(String accountNumber) {
		return new AccountNumber(accountNumber);
	}

	public List<AccountNumber> findAccounts(String principal) {
		final var member = memberService.findByEmail(principal);
		return accountService.getAccountByMemberId(member.getId())
				.stream()
				.map(Account::getAccountNumber)
				.toList();
	}
}
