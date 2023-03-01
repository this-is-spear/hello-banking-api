package numble.bankingapi.social.dto;

public record FriendResponse(
	Long userId,
	String name,
	String email
) {
}
