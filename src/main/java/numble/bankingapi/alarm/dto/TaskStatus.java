package numble.bankingapi.alarm.dto;

public enum TaskStatus {
	SUCCESS("성공"),
	FAIL("실패");

	private final String status;

	TaskStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
