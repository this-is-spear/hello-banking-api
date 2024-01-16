package bankingapi.util.filter;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import bankingapi.util.filter.PathMatcherInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import bankingapi.util.matcher.CustomPathContainer;

@ExtendWith(MockitoExtension.class)
class PathMatcherInterceptorTest {

	@Mock
	private HandlerInterceptor handlerInterceptor;
	@Mock
	private CustomPathContainer customPathContainer;
	@InjectMocks
	private PathMatcherInterceptor pathMatcherInterceptor;
	MockHttpServletRequest request;
	MockHttpServletResponse response;
	@Mock
	Object handler;

	@BeforeEach
	void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	@DisplayName("등록되지 않은 URL이면 true를 반환합니다.")
	void preHandle_returnTrue() {
		when(customPathContainer.notIncludedPath(request)).thenReturn(true);

		var actual = assertDoesNotThrow(
			() -> pathMatcherInterceptor.preHandle(request, response, handler)
		);
		assertThat(actual).isTrue();
	}

	@Test
	@DisplayName("등록되어 있는 URL이면 다음 인터셉터를 호출한다.")
	void preHandle_DoNotReturn() throws Exception {
		when(customPathContainer.notIncludedPath(request)).thenReturn(false);
		when(handlerInterceptor.preHandle(request, response, handler)).thenReturn(true);

		var actual = assertDoesNotThrow(
			() -> pathMatcherInterceptor.preHandle(request, response, handler)
		);
		assertThat(actual).isTrue();
		verify(handlerInterceptor, atLeast(1)).preHandle(request, response, handler);
	}

	@Test
	void includePathPattern() {
		doNothing().when(customPathContainer).includePathPattern("/account", HttpMethod.POST);
		assertDoesNotThrow(
			() -> pathMatcherInterceptor.includePathPattern("/account", HttpMethod.POST)
		);
		verify(customPathContainer, times(1)).includePathPattern("/account", HttpMethod.POST);
	}

	@Test
	void excludePathPattern() {
		doNothing().when(customPathContainer).excludePathPattern("/account", HttpMethod.GET);
		assertDoesNotThrow(
			() -> pathMatcherInterceptor.excludePathPattern("/account", HttpMethod.GET)
		);
		verify(customPathContainer, times(1)).excludePathPattern("/account", HttpMethod.GET);
	}
}
