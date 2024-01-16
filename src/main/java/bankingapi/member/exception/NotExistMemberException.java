package bankingapi.member.exception;

public class NotExistMemberException extends RuntimeException {
	public NotExistMemberException() {
		super("존재하지 않은 사용자입니다.");
	}
}
