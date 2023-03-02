package numble.bankingapi.social.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
	public ResponseEntity<Void> askWantToBefriends(@AuthenticationPrincipal UserDetails principal,
		@PathVariable Long someoneId) {
		socialNetworkService.askWantToBefriends(principal.getUsername(), someoneId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/friends/{requestId}/approval")
	public ResponseEntity<Void> approvalRequest(@AuthenticationPrincipal UserDetails principal,
		@PathVariable Long requestId) {
		socialNetworkService.approvalRequest(principal.getUsername(), requestId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/friends/{requestId}/rejected")
	public ResponseEntity<Void> rejectRequest(@AuthenticationPrincipal UserDetails principal,
		@PathVariable Long requestId) {
		socialNetworkService.rejectRequest(principal.getUsername(), requestId);
		return ResponseEntity.ok().build();
	}

	@GetMapping(
		value = "/friends",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<FriendResponses> findFriends(@AuthenticationPrincipal UserDetails principal) {
		return ResponseEntity.ok(socialNetworkService.findFriends(principal.getUsername()));
	}

	@GetMapping(
		value = "/friends/requests",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<AskedFriendResponses> findRequestWandToBeFriend(
		@AuthenticationPrincipal UserDetails principal) {
		return ResponseEntity.ok(socialNetworkService.findRequestWandToBeFriend(principal.getUsername()));
	}
}
