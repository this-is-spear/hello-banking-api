package numble.bankingapi.alarm.infra;

import org.springframework.stereotype.Service;

import numble.bankingapi.alarm.domain.AlarmService;

@Service
public class NumbleAlarmService implements AlarmService {
	public void notify(Long userId, String message) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
