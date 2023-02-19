package numble.bankingapi.banking.domain;

import numble.bankingapi.alarm.dto.AlarmMessage;

public interface NotifyService {
	void notify(Long userId, AlarmMessage alarmType);
}
