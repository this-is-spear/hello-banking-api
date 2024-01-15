package bankingapi.alarm.application;

import bankingapi.alarm.domain.AlarmService;
import bankingapi.alarm.dto.AlarmMessage;
import bankingapi.banking.domain.NotifyService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankingNotifyService implements NotifyService {

	private final AlarmService alarmService;

	@Override
	public void notify(Long userId, AlarmMessage alarmType) {
		alarmService.notify(userId, alarmType.toString());
	}
}
