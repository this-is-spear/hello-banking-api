package bankingapi.util.config;

import bankingapi.util.filter.IdempotentRequestInterceptor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import bankingapi.util.filter.PathMatcherInterceptor;
import bankingapi.util.matcher.CustomPathContainer;

@Component
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
	private final IdempotentRequestInterceptor idempotentRequestInterceptor;
	private final CustomPathContainer customPathContainer;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		PathMatcherInterceptor matcherInterceptor = new PathMatcherInterceptor(idempotentRequestInterceptor,
			customPathContainer).includePathPattern("/accounts/**", HttpMethod.POST);
		registry.addInterceptor(matcherInterceptor)
			.addPathPatterns();
	}
}
