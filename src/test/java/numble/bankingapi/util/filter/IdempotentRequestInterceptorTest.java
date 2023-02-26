package numble.bankingapi.util.filter;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import numble.bankingapi.banking.domain.Money;
import numble.bankingapi.banking.dto.TransferCommand;
import numble.bankingapi.idempotent.domain.IdempotentRequestHistory;
import numble.bankingapi.idempotent.domain.IdempotentRequestHistoryService;

@ExtendWith(MockitoExtension.class)
class IdempotentRequestInterceptorTest {
	private static final String IDEMPOTENT_KEY = "idempotent-key";
	private static final String IDEMPOTENT_KEY_VALUE = "SADF2K123S123DJ";
	private static final TransferCommand COMMAND = new TransferCommand("123-234-1231-34", new Money(10_000L));
	private ObjectMapper objectMapper = new ObjectMapper();
	@Mock
	private IdempotentRequestHistoryService idempotentRequestHistoryService;
	@Mock
	Object handler;
	@Mock
	ModelAndView modelAndView;
	private IdempotentRequestInterceptor idempotentRequestInterceptor;
	private MockHttpServletRequest 키가_존재하는_요청;
	private MockHttpServletRequest 키가_존재하지_않는_요청;
	private MockHttpServletResponse response;

	@BeforeEach
	void setUp() throws JsonProcessingException {
		키가_존재하는_요청 = new MockHttpServletRequest();
		키가_존재하지_않는_요청 = new MockHttpServletRequest();
		키가_존재하는_요청.addHeader(IDEMPOTENT_KEY, IDEMPOTENT_KEY_VALUE);
		initializeMockHttpServletRequest(키가_존재하는_요청);
		initializeMockHttpServletRequest(키가_존재하지_않는_요청);

		response = new MockHttpServletResponse();
		idempotentRequestInterceptor = new IdempotentRequestInterceptor(
			idempotentRequestHistoryService);
	}

	@Test
	@DisplayName("받은 적 없는 요청인 경우 요청을 수행한다.")
	void preHandle() {
		when(idempotentRequestHistoryService.isPresent(IDEMPOTENT_KEY_VALUE)).thenReturn(false);
		assertDoesNotThrow(
			() -> idempotentRequestInterceptor.preHandle(키가_존재하는_요청, response, handler)
		);
		verify(idempotentRequestHistoryService, never()).getIdempotentRequestHistory(any());
	}

	@Test
	@DisplayName("키가 없는 경우 예외가 발생한다.")
	void preHandle_notExistKey() {
		assertThatThrownBy(
			() -> idempotentRequestInterceptor.preHandle(키가_존재하지_않는_요청, response, handler)
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("키가 비어있는 경우 예외가 발생한다.")
	void preHandle_emptyKey() {
		키가_존재하지_않는_요청.addHeader(IDEMPOTENT_KEY, "");
		assertThatThrownBy(
			() -> idempotentRequestInterceptor.preHandle(키가_존재하지_않는_요청, response, handler)
		).isInstanceOf(NullPointerException.class);
	}

	@Test
	@DisplayName("이미 보낸적이 있는 요청인 경우 저장된 응답을 반환한다.")
	void preHandle_alreadyExistResponse() {
		when(idempotentRequestHistoryService.isPresent(IDEMPOTENT_KEY_VALUE)).thenReturn(true);
		when(idempotentRequestHistoryService.getIdempotentRequestHistory(IDEMPOTENT_KEY_VALUE))
			.thenReturn(new IdempotentRequestHistory(IDEMPOTENT_KEY_VALUE, HttpStatus.OK));
		assertDoesNotThrow(
			() -> idempotentRequestInterceptor.preHandle(키가_존재하는_요청, response, handler)
		);
	}

	@Test
	@DisplayName("키에 맞은 응답 값을 저장한다.")
	void postHandle() {
		assertDoesNotThrow(
			() -> idempotentRequestInterceptor.postHandle(키가_존재하는_요청, response, handler, modelAndView)
		);
		verify(idempotentRequestHistoryService, times(1)).save(any());
	}

	@Test
	@DisplayName("키가 없다면 예외가 발생한다.")
	void postHandle_notNullKey() {
		assertThatThrownBy(
			() -> idempotentRequestInterceptor.postHandle(키가_존재하지_않는_요청, response, handler, modelAndView)
		).isInstanceOf(NullPointerException.class);
	}

	private void initializeMockHttpServletRequest(MockHttpServletRequest request) throws
		JsonProcessingException {
		request.setContentType(MediaType.APPLICATION_JSON_VALUE);
		request.setContent(objectMapper.writeValueAsBytes(COMMAND));
	}
}
