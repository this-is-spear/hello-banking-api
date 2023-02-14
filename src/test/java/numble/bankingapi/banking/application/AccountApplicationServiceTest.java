package numble.bankingapi.banking.application;

import static numble.bankingapi.fixture.AccountFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import numble.bankingapi.banking.domain.Account;
import numble.bankingapi.banking.domain.AccountHistory;
import numble.bankingapi.banking.domain.AccountHistoryService;
import numble.bankingapi.banking.domain.AccountService;
import numble.bankingapi.banking.domain.HistoryType;
import numble.bankingapi.banking.dto.HistoryResponses;
import numble.bankingapi.banking.dto.TargetResponses;
import numble.bankingapi.banking.dto.TransferCommand;

@ExtendWith(MockitoExtension.class)
class AccountApplicationServiceTest {
	@Mock
	private AccountService accountService;
	@Mock
	private AccountHistoryService accountHistoryService;
	@InjectMocks
	private AccountApplicationService accountApplicationService;

	@Test
	@DisplayName("계좌 사용기록을 반환한다.")
	void getHistory() {
		AccountHistory 첫_번째_기록 = AccountHistory.builder()
			.fromAccountNumber(계좌번호)
			.toAccountNumber(계좌번호)
			.balance(이만원)
			.money(만원)
			.type(HistoryType.DEPOSIT)
			.build();
		AccountHistory 두_번째_기록 = AccountHistory.builder()
			.fromAccountNumber(계좌번호)
			.toAccountNumber(상대방_계좌번호)
			.balance(만원)
			.money(만원)
			.type(HistoryType.WITHDRAW)
			.build();

		when(accountHistoryService.findByFromAccountNumber(계좌번호)).thenReturn(List.of(첫_번째_기록, 두_번째_기록));
		HistoryResponses responses = accountApplicationService.getHistory(계좌번호.getNumber());
		assertThat(responses.historyResponses()).hasSize(2);
		assertThat(responses.historyResponses().get(0).money()).isEqualTo(만원);
	}

	@Test
	@DisplayName("계좌에 금액을 입금한다.")
	void deposit() {
		Account 계좌 = Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(2L)
			.build();

		when(accountService.getAccountByAccountNumber(계좌번호)).thenReturn(계좌);
		doNothing().when(accountService).depositMoney(계좌, 만원);
		accountApplicationService.deposit(계좌번호.getNumber(), 만원);
	}

	@Test
	@DisplayName("계좌에 금액을 출금한다.")
	void withdraw() {
		Account 계좌 = Account.builder()
			.accountNumber(계좌번호)
			.balance(이만원)
			.userId(2L)
			.build();

		when(accountService.getAccountByAccountNumber(계좌번호)).thenReturn(계좌);
		doNothing().when(accountService).withdrawMoney(계좌, 만원);
		accountApplicationService.withdraw(계좌번호.getNumber(), 만원);
	}

	@Test
	@DisplayName("계좌 이체한다.")
	void transfer() {
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

		when(accountService.getAccountByAccountNumber(계좌번호)).thenReturn(계좌);
		when(accountService.getAccountByAccountNumber(상대방_계좌번호)).thenReturn(상대방_계좌);
		doNothing().when(accountService).transferMoney(계좌, 상대방_계좌, 만원);
		accountApplicationService.transfer(계좌번호.getNumber(), new TransferCommand(상대방_계좌번호.getNumber(), 만원));
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

		when(accountService.findAll()).thenReturn(List.of(계좌, 상대방_계좌));
		TargetResponses responses = accountApplicationService.getTargets();
		assertThat(responses.targets()).hasSize(2);
	}
}
