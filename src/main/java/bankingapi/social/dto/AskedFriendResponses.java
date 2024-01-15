package bankingapi.social.dto;

import java.util.List;

public record AskedFriendResponses(
	List<AskedFriendResponse> askedFriendResponses
) {
	@Override
	public List<AskedFriendResponse> askedFriendResponses() {
		return List.copyOf(askedFriendResponses);
	}
}
