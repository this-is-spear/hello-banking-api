package numble.bankingapi.member.exception;

public class InvalidFormatException extends RuntimeException {

	public InvalidFormatException(String message) {
		super(message);
	}

	public static InvalidFormatException invalidEmail() {
		return new InvalidFormatException("이메일 형식이 아닙니다.");
	}

	public static InvalidFormatException emptyPassword(){
		return new InvalidFormatException("비밀번호는 비어있을 수 없습니다.");
	}

	public static InvalidFormatException emptyEmail(){
		return new InvalidFormatException("이메일은 비어있을 수 없습니다.");
	}

	public static InvalidFormatException emptyName(){
		return new InvalidFormatException("이름은 비어있을 수 없습니다.");
	}
}
