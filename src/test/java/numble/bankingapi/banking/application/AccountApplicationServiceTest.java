package numble.bankingapi.banking.application;

import static numble.bankingapi.fixture.AccountFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import numble.bankingapi.alarm.dto.AlarmMessage;
import numble.bankingapi.alarm.dto.TaskStatus;
import numble.bankingapi.alarm.dto.TaskType;
import numble.bankingapi.banking.domain.Account;
import numble.bankingapi.banking.domain.AccountHistory;
import numble.bankingapi.banking.domain.AccountService;
import numble.bankingapi.banking.domain.HistoryType;
import numble.bankingapi.banking.domain.NotifyService;
import numble.bankingapi.banking.dto.HistoryResponses;
import numble.bankingapi.banking.dto.TargetResponses;
import numble.bankingapi.banking.dto.TransferCommand;
import numble.bankingapi.banking.exception.InvalidMemberException;
import numble.bankingapi.member.domain.Member;
import numble.bankingapi.member.domain.MemberService;
import numble.bankingapi.member.domain.RoleType;

@ExtendWith(MockitoExtension.class)
class AccountApplicationServiceTest {
	private static final long 상대방_ID = 3L;
	private static final long 사용자_ID = 2L;
	private static final String EMAIL = "member@email.com";
	private static final AccountHistory 첫_번째_기록 = AccountHistory.builder()
		.fromAccountNumber(계좌번호)
		.toAccountNumber(계좌번호)
		.balance(이만원)
		.money(만원)
		.type(HistoryType.DEPOSIT)
		.build();
	private static final AccountHistory 두_번째_기록 = AccountHistory.builder()
		.fromAccountNumber(계좌번호)
		.toAccountNumber(상대방_계좌번호)
		.balance(만원)
		.money(만원)
		.type(HistoryType.WITHDRAW)
		.build();
	private static final Account 계좌 = Account.builder()
		.accountNumber(계좌번호)
		.balance(이만원)
		.userId(사용자_ID)
		.build();
	@Mock
	private AccountService accountService;
	@Mock
	private ConcurrencyFacade concurrencyFacade;
	@Mock
	private NotifyService notifyService;
	@Mock
	private MemberService memberService;
	@InjectMocks
	private AccountApplicationService accountApplicationService;

	@Test
	@DisplayName("계좌 사용기록을 반환한다.")
	void getHistory() {
		when(accountService.getAccountByAccountNumber(계좌번호)).thenReturn(계좌);
		when(accountService.findAccountHistoriesByFromAccountNumber(EMAIL, 계좌번호)).thenReturn(List.of(첫_번째_기록, 두_번째_기록));

		HistoryResponses responses = assertDoesNotThrow(
			() -> accountApplicationService.getHistory(EMAIL, 계좌번호.getNumber())
		);

		assertThat(responses.historyResponses()).hasSize(2);
		assertThat(responses.historyResponses().get(0).money()).isEqualTo(만원);
	}

	@Test
	@DisplayName("계좌 사용기록을 반환할 때, 해당 사용자가 아니면 예외가 발생한다.")
	void getHistory_accessInvalidMember() {
		long 본인아님 = 231L;
		when(accountService.getAccountByAccountNumber(계좌번호)).thenReturn(계좌);
		when(memberService.findByEmail(EMAIL)).thenReturn(
			new Member(본인아님, EMAIL, "name", "password", List.of(RoleType.ROLE_MEMBER.name())));

		assertThatThrownBy(
			() -> accountApplicationService.getHistory(EMAIL, 계좌번호.getNumber())
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌에 금액을 입금한다.")
	void deposit() {
		Account 계좌 = Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(사용자_ID)
			.build();

		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		doNothing().when(accountService).depositMoney(EMAIL, 계좌.getAccountNumber(), 만원);
		doNothing().when(notifyService).notify(계좌.getUserId(), new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT));
		assertDoesNotThrow(
			() -> accountApplicationService.deposit(EMAIL, 계좌번호.getNumber(), 만원)
		);
	}

	@Test
	@DisplayName("입금할 때, 해당 사용자가 아니면 예외가 발생한다.")
	void deposit_accessInvalidMember() {
		long 본인아님 = 231L;
		Account 계좌 = Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(사용자_ID)
			.build();

		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		when(memberService.findByEmail(EMAIL)).thenReturn(
			new Member(본인아님, EMAIL, "name", "password", List.of(RoleType.ROLE_MEMBER.name())));
		assertThatThrownBy(
			() -> accountApplicationService.deposit(EMAIL, 계좌번호.getNumber(), 만원)
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌에 금액을 출금한다.")
	void withdraw() {
		Account 계좌 = Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(사용자_ID)
			.build();

		doNothing().when(accountService).withdrawMoney(EMAIL, 계좌.getAccountNumber(), 만원);
		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		doNothing().when(notifyService).notify(계좌.getUserId(), new AlarmMessage(TaskStatus.SUCCESS, TaskType.WITHDRAW));
		assertDoesNotThrow(
			() -> accountApplicationService.withdraw(EMAIL, 계좌번호.getNumber(), 만원)
		);
	}

	@Test
	@DisplayName("출금할 때, 해당 사용자가 아니면 예외가 발생한다.")
	void withdraw_accessInvalidMember() {
		long 본인아님 = 231L;
		Account 계좌 = Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(사용자_ID)
			.build();

		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		when(memberService.findByEmail(EMAIL)).thenReturn(
			new Member(본인아님, EMAIL, "name", "password", List.of(RoleType.ROLE_MEMBER.name())));
		assertThatThrownBy(
			() -> accountApplicationService.withdraw(EMAIL, 계좌번호.getNumber(), 만원)
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌 이체한다.")
	void transfer() {
		Account 계좌 = Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(사용자_ID)
			.build();

		Account 상대방_계좌 = Account.builder()
			.accountNumber(상대방_계좌번호)
			.balance(만원)
			.userId(상대방_ID)
			.build();

		doNothing().when(concurrencyFacade)
			.transferWithLock(EMAIL, 계좌.getAccountNumber(), 상대방_계좌.getAccountNumber(), 만원);
		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		when(accountService.getAccountByAccountNumber(상대방_계좌.getAccountNumber())).thenReturn(상대방_계좌);

		doNothing().when(notifyService)
			.notify(계좌.getUserId(), new AlarmMessage(TaskStatus.SUCCESS, TaskType.TRANSFER));
		doNothing().when(notifyService)
			.notify(상대방_계좌.getUserId(), new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT));

		accountApplicationService.transfer(EMAIL, 계좌번호.getNumber(), new TransferCommand(상대방_계좌번호.getNumber(), 만원));
	}

	@Test
	@DisplayName("계좌 이체할 상대방을 찾는다.")
	void getTargets() {
		Account 계좌 = Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(2L)
			.build();

		Account 상대방_계좌 = Account.builder()
			.accountNumber(상대방_계좌번호)
			.balance(만원)
			.userId(2L)
			.build();

		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		when(memberService.findByEmail(EMAIL)).thenReturn(
			new Member(사용자_ID, EMAIL, "name", "password", List.of(RoleType.ROLE_MEMBER.name())));
		when(accountService.findAll()).thenReturn(List.of(계좌, 상대방_계좌));
		TargetResponses responses = assertDoesNotThrow(
			() -> accountApplicationService.getTargets(EMAIL, 계좌.getAccountNumber().getNumber()));
		assertThat(responses.targets()).hasSize(2);
	}

	@Test
	@DisplayName("계좌 이체할 상대방을 찾는다.")
	void getTargets_accessInvalidMember() {
		long 본인아님 = 231L;
		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		when(memberService.findByEmail(EMAIL)).thenReturn(
			new Member(본인아님, EMAIL, "name", "password", List.of(RoleType.ROLE_MEMBER.name())));
		assertThatThrownBy(
			() -> accountApplicationService.getTargets(EMAIL, 계좌.getAccountNumber().getNumber())
		).isInstanceOf(InvalidMemberException.class);
	}
}
