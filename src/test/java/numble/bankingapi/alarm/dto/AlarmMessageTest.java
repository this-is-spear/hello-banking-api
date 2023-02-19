package numble.bankingapi.alarm.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlarmMessageTest {

	@Test
	@DisplayName("알람에 전송할 메시지를 생성한다.")
	void createAlarmMessage() {
		assertDoesNotThrow(
			() -> new AlarmMessage(TaskStatus.SUCCESS, TaskType.DEPOSIT)
		);
	}

	@Test
	@DisplayName("알람 실패 메시지륾 문자열로 받는다.")
	void getMessageUsingAlarmMessage() {
		AlarmMessage message = new AlarmMessage(TaskStatus.FAIL, TaskType.DEPOSIT);
		Assertions.assertThat(message.toString()).isEqualTo("입금 실패");
	}
}
