package bankingapi.alarm.dto;

public record AlarmMessage(
	TaskStatus status,
	TaskType type
) {
	@Override
	public String toString() {
		return String.format("%s %s", type.getType(), status.getStatus());
	}
}
