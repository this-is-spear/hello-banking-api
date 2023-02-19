package numble.bankingapi.alarm.application;

import org.springframework.stereotype.Service;

import numble.bankingapi.alarm.domain.AlarmService;

public class FakeNumbleAlarmService implements AlarmService {
	public void notify(Long userId, String message) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
