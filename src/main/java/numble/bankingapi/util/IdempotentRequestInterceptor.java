package numble.bankingapi.util;

import static org.springframework.http.MediaType.*;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import numble.bankingapi.idempotent.domain.IdempotentRequestHistory;
import numble.bankingapi.idempotent.domain.IdempotentRequestHistoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotentRequestInterceptor implements HandlerInterceptor {
	private static final String IDEMPOTENT_KEY = "idempotent-key";
	private final IdempotentRequestHistoryService idempotentRequestHistoryService;
	private final ObjectMapper objectMapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		Exception {
		final var cachingRequest = new ContentCachingRequestWrapper(request);
		var key = request.getHeader(IDEMPOTENT_KEY);

		validKey(key);

		if (validRequestBodyIsJson(cachingRequest)) {
			log.info("Key is {}, Request Body : {}", key, parseJson(cachingRequest.getContentAsByteArray()));
		}

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
		final var cachingResponse = new ContentCachingResponseWrapper(response);
		final var key = request.getHeader(IDEMPOTENT_KEY);
		JsonNode jsonNode = null;
		validKey(key);

		if (validResponseBodyIsJson(cachingResponse)) {
			jsonNode = parseJson(cachingResponse.getContentAsByteArray());
			log.info("Response Body : {}", jsonNode);
		}

		final var history = new IdempotentRequestHistory(key, HttpStatus.resolve(response.getStatus()),
			objectMapper.writeValueAsString(jsonNode));
		idempotentRequestHistoryService.save(history);

		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	private void sendAlreadyExistResponse(HttpServletResponse response, String key) throws IOException {
		var requestHistory = idempotentRequestHistoryService.getIdempotentRequestHistory(key);
		response.setStatus(requestHistory.getResponseStatus().value());
		response.setCharacterEncoding("UTF-8");
		var writer = response.getWriter();
		writer.print(requestHistory.getResponseBody());
		writer.flush();
	}

	private void validKey(String key) {
		if (key == null || key.isBlank()) {
			throw new NullPointerException();
		}
	}

	private boolean validResponseBodyIsJson(ContentCachingResponseWrapper cachingResponse) {
		return cachingResponse.getContentType() != null
			&& cachingResponse.getContentType().contains(APPLICATION_JSON_VALUE)
			&& cachingResponse.getContentAsByteArray().length != 0;
	}

	private boolean validRequestBodyIsJson(ContentCachingRequestWrapper cachingRequest) {
		return cachingRequest.getContentType() != null
			&& cachingRequest.getContentType().contains(APPLICATION_JSON_VALUE)
			&& cachingRequest.getContentAsByteArray().length != 0;
	}

	private JsonNode parseJson(byte[] cachingRequest) throws IOException {
		return objectMapper.readTree(cachingRequest);
	}
}
