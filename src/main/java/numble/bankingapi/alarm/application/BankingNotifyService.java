package numble.bankingapi.alarm.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.alarm.domain.AlarmService;
import numble.bankingapi.alarm.dto.AlarmMessage;
import numble.bankingapi.banking.domain.NotifyService;

@Service
@RequiredArgsConstructor
public class BankingNotifyService implements NotifyService {

	private final AlarmService alarmService;

	@Override
	public void notify(Long userId, AlarmMessage alarmType) {
		alarmService.notify(userId, alarmType.toString());
	}
}
