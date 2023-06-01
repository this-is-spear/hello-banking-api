package numble.bankingapi.util.config;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import numble.bankingapi.util.filter.IdempotentRequestInterceptor;
import numble.bankingapi.util.filter.PathMatcherInterceptor;
import numble.bankingapi.util.matcher.CustomPathContainer;

@Component
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
	private final IdempotentRequestInterceptor idempotentRequestInterceptor;
	private final CustomPathContainer customPathContainer;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		PathMatcherInterceptor matcherInterceptor = new PathMatcherInterceptor(idempotentRequestInterceptor,
			customPathContainer).includePathPattern("/account/**", HttpMethod.POST);
		registry.addInterceptor(matcherInterceptor)
			.addPathPatterns();
	}
}
