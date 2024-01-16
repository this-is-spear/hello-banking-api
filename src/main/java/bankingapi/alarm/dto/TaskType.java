package bankingapi.alarm.dto;

public enum TaskType {
	WITHDRAW("출금"),
	DEPOSIT("입금"),
	TRANSFER("이체");

	private final String type;

	TaskType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
