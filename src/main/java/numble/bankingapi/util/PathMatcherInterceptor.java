package numble.bankingapi.util;

import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PathMatcherInterceptor implements HandlerInterceptor {

	private final HandlerInterceptor handlerInterceptor;
	private final CustomPathContainer customPathContainer;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
		Object handler) throws Exception {

		if (customPathContainer.notIncludedPath(request)) {
			return true;
		}

		log.info("Include Path : {}", request.getPathInfo());
		return handlerInterceptor.preHandle(request, response, handler);
	}

	public PathMatcherInterceptor includePathPattern(String pathPattern, HttpMethod pathMethod) {
		customPathContainer.includePathPattern(pathPattern, pathMethod);
		return this;
	}

	public PathMatcherInterceptor excludePathPattern(String pathPattern, HttpMethod pathMethod) {
		customPathContainer.excludePathPattern(pathPattern, pathMethod);
		return this;
	}
}
