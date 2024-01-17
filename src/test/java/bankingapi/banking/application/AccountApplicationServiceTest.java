package bankingapi.banking.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import bankingapi.fixture.AccountFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import bankingapi.alarm.dto.AlarmMessage;
import bankingapi.alarm.dto.TaskStatus;
import bankingapi.alarm.dto.TaskType;
import bankingapi.banking.domain.Account;
import bankingapi.banking.domain.AccountHistory;
import bankingapi.banking.domain.AccountService;
import bankingapi.banking.domain.HistoryType;
import bankingapi.banking.domain.NotifyService;
import bankingapi.banking.dto.TransferCommand;
import bankingapi.banking.exception.InvalidMemberException;
import bankingapi.member.domain.Member;
import bankingapi.member.domain.MemberService;
import bankingapi.member.domain.RoleType;
import bankingapi.social.domain.Friend;
import bankingapi.social.domain.FriendService;

@ExtendWith(MockitoExtension.class)
class AccountApplicationServiceTest {
	private static final long 상대방_ID = 3L;
	private static final long 사용자_ID = 2L;
	private static final String EMAIL = "member@email.com";
	private static final Member 상대방 = new Member(상대방_ID, "email@email.com", "상대방", "pass",
		List.of(RoleType.ROLE_MEMBER.name()));
	private static final Member 사용자 = new Member(사용자_ID, EMAIL, "name", "password",
		List.of(RoleType.ROLE_MEMBER.name()));
	private static final AccountHistory 첫_번째_기록 = AccountHistory.builder()
		.fromAccountNumber(AccountFixture.계좌번호)
		.toAccountNumber(AccountFixture.계좌번호)
		.balance(AccountFixture.이만원)
		.money(AccountFixture.만원)
		.type(HistoryType.DEPOSIT)
		.build();
	private static final AccountHistory 두_번째_기록 = AccountHistory.builder()
		.fromAccountNumber(AccountFixture.계좌번호)
		.toAccountNumber(AccountFixture.상대방_계좌번호)
		.balance(AccountFixture.만원)
		.money(AccountFixture.만원)
		.type(HistoryType.WITHDRAW)
		.build();
	private static final Account 계좌 = Account.builder()
		.accountNumber(AccountFixture.계좌번호)
		.balance(AccountFixture.이만원)
		.userId(사용자_ID)
		.build();

    private static final Account 자신의_계좌 = Account.builder()
            .accountNumber(AccountFixture.자신의_계좌반호)
            .balance(AccountFixture.이만원)
            .userId(사용자_ID)
            .build();

    private static final Account 상대방_계좌 = Account.builder()
		.accountNumber(AccountFixture.상대방_계좌번호)
		.balance(AccountFixture.만원)
		.userId(상대방_ID)
		.build();
	@Mock
	private FriendService friendService;
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
		when(memberService.findByEmail(사용자.getEmail())).thenReturn(사용자);
		when(accountService.getAccountByAccountNumber(AccountFixture.계좌번호)).thenReturn(계좌);
		when(accountService.findAccountHistoriesByFromAccountNumber(계좌)).thenReturn(List.of(첫_번째_기록, 두_번째_기록));

		var responses = assertDoesNotThrow(
			() -> accountApplicationService.getHistory(EMAIL, AccountFixture.계좌번호.getNumber())
		);

		assertThat(responses.historyResponses()).hasSize(2);
		assertThat(responses.historyResponses().get(0).money()).isEqualTo(AccountFixture.만원);
	}

	@Test
	@DisplayName("계좌에 금액을 입금한다.")
	void deposit() {
		when(memberService.findByEmail(사용자.getEmail())).thenReturn(사용자);
		when(accountService.getAccountByAccountNumber(AccountFixture.계좌번호)).thenReturn(계좌);
		doNothing().when(notifyService).notify(계좌.getUserId(), new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT));
		assertDoesNotThrow(
			() -> accountApplicationService.deposit(EMAIL, AccountFixture.계좌번호.getNumber(), AccountFixture.만원)
		);
	}

	@Test
	@DisplayName("계좌에 금액을 입금한할 때 본인이 아니면 예외가 발생한다.")
	void deposit_accessInvalidMember() {
		when(memberService.findByEmail(사용자.getEmail())).thenReturn(상대방);
		when(accountService.getAccountByAccountNumber(AccountFixture.계좌번호)).thenReturn(계좌);
		assertThatThrownBy(
			() -> accountApplicationService.deposit(EMAIL, AccountFixture.계좌번호.getNumber(), AccountFixture.만원)
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌에 금액을 출금한다.")
	void withdraw() {
		when(memberService.findByEmail(사용자.getEmail())).thenReturn(사용자);
		when(accountService.getAccountByAccountNumber(AccountFixture.계좌번호)).thenReturn(계좌);
		doNothing().when(notifyService).notify(사용자_ID, new AlarmMessage(TaskStatus.SUCCESS, TaskType.WITHDRAW));
		assertDoesNotThrow(
			() -> accountApplicationService.withdraw(EMAIL, AccountFixture.계좌번호.getNumber(), AccountFixture.만원)
		);
	}

	@Test
	@DisplayName("계좌에 금액을 출금할 때 본인이 아니면 예외가 발생한다.")
	void withdraw_accessInvalidMember() {
		when(memberService.findByEmail(사용자.getEmail())).thenReturn(상대방);
		when(accountService.getAccountByAccountNumber(AccountFixture.계좌번호)).thenReturn(계좌);
		assertThatThrownBy(
			() -> accountApplicationService.withdraw(EMAIL, AccountFixture.계좌번호.getNumber(), AccountFixture.만원)
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌 이체한다.")
	void transfer() {
		when(memberService.findByEmail(사용자.getEmail())).thenReturn(사용자);
		doNothing().when(concurrencyFacade)
			.transferWithLock(계좌.getAccountNumber(), 상대방_계좌.getAccountNumber(), AccountFixture.만원);
		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		when(accountService.getAccountByAccountNumber(상대방_계좌.getAccountNumber())).thenReturn(상대방_계좌);

		doNothing().when(notifyService)
			.notify(계좌.getUserId(), new AlarmMessage(TaskStatus.SUCCESS, TaskType.TRANSFER));
		doNothing().when(notifyService)
			.notify(상대방_계좌.getUserId(), new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT));

		assertDoesNotThrow(
			() -> accountApplicationService.transfer(EMAIL, AccountFixture.계좌번호.getNumber(),
				new TransferCommand(AccountFixture.상대방_계좌번호.getNumber(), AccountFixture.만원))
		);
	}

	@Test
	@DisplayName("이체할 때 본인이 아니면 안된다.")
	void transfer_accessInvalidMember() {
		when(memberService.findByEmail(사용자.getEmail())).thenReturn(상대방);
		when(accountService.getAccountByAccountNumber(AccountFixture.계좌번호)).thenReturn(계좌);
		when(accountService.getAccountByAccountNumber(계좌.getAccountNumber())).thenReturn(계좌);
		when(accountService.getAccountByAccountNumber(상대방_계좌.getAccountNumber())).thenReturn(상대방_계좌);

		assertThatThrownBy(
			() -> accountApplicationService.transfer(EMAIL, AccountFixture.계좌번호.getNumber(),
				new TransferCommand(AccountFixture.상대방_계좌번호.getNumber(), AccountFixture.만원))
		).isInstanceOf(InvalidMemberException.class);
	}

	@Test
	@DisplayName("계좌 이체할 상대방을 찾는다.")
	void getTargets() {
		when(memberService.findByEmail(EMAIL)).thenReturn(사용자);
		when(friendService.findFriends(사용자_ID)).thenReturn(List.of(new Friend(사용자_ID, 상대방_ID)));
		when(memberService.findAllById(List.of(상대방_ID))).thenReturn(List.of(상대방));
		when(accountService.getFriendAccounts(List.of(상대방_ID))).thenReturn(List.of(상대방_계좌));

		var responses = assertDoesNotThrow(
			() -> accountApplicationService.getTargets(EMAIL));
		assertThat(responses.targets()).hasSize(1);
	}

	@Test
	@DisplayName("자신의 계좌를 조회한다.")
	void findAccounts() {
        when(memberService.findByEmail(EMAIL)).thenReturn(사용자);
		when(accountService.getAccountByMemberId(사용자_ID)).thenReturn(List.of(자신의_계좌, 계좌));

		var responses = assertDoesNotThrow(
			() -> accountApplicationService.findAccounts(EMAIL)
		);

		assertThat(responses).hasSize(2);
	}
}
