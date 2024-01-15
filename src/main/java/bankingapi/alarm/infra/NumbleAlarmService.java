package bankingapi.alarm.infra;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import bankingapi.alarm.domain.AlarmService;

@Service
public class NumbleAlarmService implements AlarmService {
	@Async
	public void notify(Long userId, String message) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
