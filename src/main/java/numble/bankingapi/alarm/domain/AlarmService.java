package numble.bankingapi.alarm.domain;

public interface AlarmService {
	void notify(Long userId, String message);
}
