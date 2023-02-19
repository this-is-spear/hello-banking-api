package numble.bankingapi.alarm.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import numble.bankingapi.alarm.dto.AlarmMessage;
import numble.bankingapi.alarm.dto.TaskStatus;
import numble.bankingapi.alarm.dto.TaskType;

class BankingNotifyServiceTest {

	private final BankingNotifyService bankingNotifyService = new BankingNotifyService(new FakeNumbleAlarmService());

	@Test
	@DisplayName("알람 메시지를 전송한다.")
	void notifyMessage() {
		AlarmMessage message = new AlarmMessage(TaskStatus.FAIL, TaskType.DEPOSIT);
		bankingNotifyService.notify(2L, message);
	}
}
