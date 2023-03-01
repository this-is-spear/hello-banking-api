package numble.bankingapi.member.dto;

public record RegisterCommand(
	String email,
	String name,
	String password
) {
}
