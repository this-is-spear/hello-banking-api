package numble.bankingapi.member.application;

public class DuplicateEmailException extends RuntimeException {
	public DuplicateEmailException() {
		super("이메일 중복입니다.");
	}
}
