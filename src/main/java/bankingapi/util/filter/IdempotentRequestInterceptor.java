package bankingapi.util.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import bankingapi.idempotent.domain.IdempotentRequestHistory;
import bankingapi.idempotent.domain.IdempotentRequestHistoryService;
import bankingapi.idempotent.exception.InvalidIdempotencyKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentRequestInterceptor implements HandlerInterceptor {
	private static final String IDEMPOTENT_KEY = "Idempotency-Key";
	private final IdempotentRequestHistoryService idempotentRequestHistoryService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		final var key = request.getHeader(IDEMPOTENT_KEY);
		validKey(key);

		if (idempotentRequestHistoryService.isPresent(key)) {
			log.info("Already Request Key is : {}", key);
			sendAlreadyExistResponse(response, key);
			return true;
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
		ModelAndView modelAndView) throws Exception {
		final var key = request.getHeader(IDEMPOTENT_KEY);
		validKey(key);

		final var history = new IdempotentRequestHistory(key, HttpStatus.resolve(response.getStatus()));
		idempotentRequestHistoryService.save(history);

		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	private void sendAlreadyExistResponse(HttpServletResponse response, String key) throws IOException {
		var requestHistory = idempotentRequestHistoryService.getIdempotentRequestHistory(key);
		response.setStatus(requestHistory.getResponseStatus().value());
	}

	private void validKey(String key) {
		if (key == null || key.isBlank()) {
			throw new InvalidIdempotencyKey();
		}
	}
}
