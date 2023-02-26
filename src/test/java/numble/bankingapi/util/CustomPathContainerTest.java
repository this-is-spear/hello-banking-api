package numble.bankingapi.util;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;

class CustomPathContainerTest {

	CustomPathContainer customPathContainer;
	MockHttpServletRequest POST_요청;
	MockHttpServletRequest GET_요청;

	@BeforeEach
	void setUp() {
		customPathContainer = new CustomPathContainer();
		POST_요청 = new MockHttpServletRequest("POST", "/account");
		POST_요청.setServletPath("/account");

		GET_요청 = new MockHttpServletRequest("GET", "/account");
		GET_요청.setServletPath("/account");
	}

	@Test
	@DisplayName("includePathPattern 메서드를 이용해 포함시키고 notIncludedPath 메서드로 확인하면 false 를 반환한다.")
	void includeAndJudge() {
		assertAll(
			() -> assertDoesNotThrow(
				() -> customPathContainer.includePathPattern("/account", HttpMethod.POST)
			),
			() -> assertThat(customPathContainer.notIncludedPath(POST_요청)).isFalse(),
			() -> assertThat(customPathContainer.notIncludedPath(GET_요청)).isTrue()
		);
	}

	@Test
	@DisplayName("excludePathPattern 메서드를 이용해 포함시키고 notIncludedPath 메서드로 확인하면 true 를 반환한다.")
	void excludeAndJudge() {
		assertAll(
			() -> assertDoesNotThrow(
				() -> customPathContainer.excludePathPattern("/account", HttpMethod.POST)
			),
			() -> assertThat(customPathContainer.notIncludedPath(POST_요청)).isTrue(),
			() -> assertThat(customPathContainer.notIncludedPath(GET_요청)).isTrue()
		);
	}
}
