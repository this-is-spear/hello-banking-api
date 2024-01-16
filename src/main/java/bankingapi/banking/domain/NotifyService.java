package bankingapi.banking.domain;

import bankingapi.alarm.dto.AlarmMessage;

public interface NotifyService {
	void notify(Long userId, AlarmMessage alarmType);
}
