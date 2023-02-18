package numble.bankingapi.member.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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

	@PostMapping("/register")
	public ResponseEntity<Void> registerMember(@RequestBody RegisterCommand registerCommand) {
		memberApplicationService.registerMember(registerCommand);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/me")
	public ResponseEntity<MemberResponse> me() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User principal = (User)authentication.getPrincipal();
		MemberResponse memberResponse = memberApplicationService.getMember(principal.getUsername());
		return ResponseEntity.ok(memberResponse);
	}
}
