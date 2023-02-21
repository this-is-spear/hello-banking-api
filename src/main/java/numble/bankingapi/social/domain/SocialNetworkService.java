package numble.bankingapi.social.domain;

import org.springframework.stereotype.Service;

import numble.bankingapi.social.dto.AskedFriendResponses;
import numble.bankingapi.social.dto.FriendResponses;

@Service
public class SocialNetworkService {
	public void askWantToBefriends(String principal, Long someoneId) {

	}

	public void approvalRequest(String principal, Long requestId) {

	}

	public void rejectRequest(String principal, Long requestId) {

	}

	public FriendResponses findFriends(String principal) {
		return null;
	}

	public AskedFriendResponses findRequestWandToBeFriend(String principal) {
		return null;
	}
}
