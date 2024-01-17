package bankingapi.alarm.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import bankingapi.alarm.domain.AlarmService;

@Slf4j
@Service
public class NumbleAlarmService implements AlarmService {
	@Async
	public void notify(Long userId, String message) {
        log.info("send message user id is {}, {}", userId, message);
	}
}
