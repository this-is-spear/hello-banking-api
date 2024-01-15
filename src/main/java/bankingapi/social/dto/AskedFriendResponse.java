package bankingapi.social.dto;

public record AskedFriendResponse(
	Long requestId,
	Long fromUserId,
	String name,
	String email
) {
}
