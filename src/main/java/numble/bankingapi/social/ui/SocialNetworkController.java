package numble.bankingapi.social.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.social.dto.AskedFriendResponses;
import numble.bankingapi.social.dto.FriendResponses;
import numble.bankingapi.social.domain.SocialNetworkService;

@RestController
@RequiredArgsConstructor
public class SocialNetworkController {

	private final SocialNetworkService socialNetworkService;

	@PostMapping("/member/friends/{someoneId}")
	public ResponseEntity<Void> askWantToBefriends(@PathVariable Long someoneId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		socialNetworkService.askWantToBefriends((String)authentication.getPrincipal(), someoneId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/member/friends/{requestId}/approval")
	public ResponseEntity<Void> approvalRequest(@PathVariable Long requestId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		socialNetworkService.approvalRequest((String)authentication.getPrincipal(), requestId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/member/friends/{requestId}/rejected")
	public ResponseEntity<Void> rejectRequest(@PathVariable Long requestId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		socialNetworkService.rejectRequest((String)authentication.getPrincipal(), requestId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/member/friends")
	public ResponseEntity<FriendResponses> findFriends() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		FriendResponses friendResponses = socialNetworkService.findFriends((String)authentication.getPrincipal());
		return ResponseEntity.ok(friendResponses);
	}

	@GetMapping("/member/friends/requests")
	public ResponseEntity<AskedFriendResponses> findRequestWandToBeFriend() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		AskedFriendResponses friendResponses = socialNetworkService.findRequestWandToBeFriend((String)authentication.getPrincipal());
		return ResponseEntity.ok(friendResponses);
	}
}
