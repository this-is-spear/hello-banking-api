package numble.bankingapi.member.ui;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import numble.bankingapi.member.application.MemberApplicationService;
import numble.bankingapi.member.dto.MemberResponse;
import numble.bankingapi.member.dto.RegisterCommand;

@RestController
@RequestMapping("members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberApplicationService memberApplicationService;

	@PostMapping(
		value = "/register",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Void> registerMember(@RequestBody RegisterCommand registerCommand) {
		memberApplicationService.registerMember(registerCommand);
		return ResponseEntity.ok().build();
	}

	@GetMapping(
		value = "/me",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<MemberResponse> me(@AuthenticationPrincipal UserDetails principal) {
		return ResponseEntity.ok(memberApplicationService.getMember(principal.getUsername()));
	}
}
