package bankingapi.idempotent.exception;

import bankingapi.idempotent.dto.ExceptionMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InvalidIdempotencyAdvice {

	@ExceptionHandler(value = InvalidIdempotencyKey.class)
	public ResponseEntity<ExceptionMessage> invalidIdempotencyMessage() {
		var message = new ExceptionMessage(
			"https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/",
			"Idempotency-Key is missing",
			"This operation is idempotent and it requires correct usage of Idempotency Key."
		);
		return ResponseEntity.badRequest().body(message);
	}
}
