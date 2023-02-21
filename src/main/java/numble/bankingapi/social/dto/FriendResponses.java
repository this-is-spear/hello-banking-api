package numble.bankingapi.social.dto;

import java.util.List;

public record FriendResponses(
	List<FriendResponse> friendResponses
) {

	@Override
	public List<FriendResponse> friendResponses() {
		return List.copyOf(friendResponses);
	}
}
