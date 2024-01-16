package bankingapi.alarm.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bankingapi.alarm.dto.AlarmMessage;
import bankingapi.alarm.dto.TaskStatus;
import bankingapi.alarm.dto.TaskType;

class BankingNotifyServiceTest {

	private final BankingNotifyService bankingNotifyService = new BankingNotifyService(new FakeNumbleAlarmService());

	@Test
	@DisplayName("알람 메시지를 전송한다.")
	void notifyMessage() {
		var message = new AlarmMessage(TaskStatus.FAIL, TaskType.DEPOSIT);
		bankingNotifyService.notify(2L, message);
	}
}
