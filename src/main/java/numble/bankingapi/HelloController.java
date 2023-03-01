package numble.bankingapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public final class HelloController {

	@GetMapping("hello")
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok("hello");
	}
}
