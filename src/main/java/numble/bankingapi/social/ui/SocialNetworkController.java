package numble.bankingapi.social.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.social.domain.SocialNetworkService;
import numble.bankingapi.social.dto.AskedFriendResponses;
import numble.bankingapi.social.dto.FriendResponses;

@RestController
@RequestMapping("members")
@RequiredArgsConstructor
public class SocialNetworkController {

	private final SocialNetworkService socialNetworkService;

	@PostMapping("/friends/{someoneId}")
	public ResponseEntity<Void> askWantToBefriends(@PathVariable Long someoneId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User principal = (User)authentication.getPrincipal();
		socialNetworkService.askWantToBefriends(principal.getUsername(), someoneId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/friends/{requestId}/approval")
	public ResponseEntity<Void> approvalRequest(@PathVariable Long requestId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User principal = (User)authentication.getPrincipal();
		socialNetworkService.approvalRequest(principal.getUsername(), requestId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/friends/{requestId}/rejected")
	public ResponseEntity<Void> rejectRequest(@PathVariable Long requestId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User principal = (User)authentication.getPrincipal();
		socialNetworkService.rejectRequest(principal.getUsername(), requestId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/friends")
	public ResponseEntity<FriendResponses> findFriends() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User principal = (User)authentication.getPrincipal();
		FriendResponses friendResponses = socialNetworkService.findFriends(principal.getUsername());
		return ResponseEntity.ok(friendResponses);
	}

	@GetMapping("/friends/requests")
	public ResponseEntity<AskedFriendResponses> findRequestWandToBeFriend() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User principal = (User)authentication.getPrincipal();
		AskedFriendResponses friendResponses = socialNetworkService.findRequestWandToBeFriend(principal.getUsername());
		return ResponseEntity.ok(friendResponses);
	}
}
