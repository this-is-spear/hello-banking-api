package numble.bankingapi.util;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {
	private final IdempotentRequestInterceptor idempotentRequestInterceptor;
	private final CustomPathContainer customPathContainer;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		PathMatcherInterceptor matcherInterceptor = new PathMatcherInterceptor(idempotentRequestInterceptor,
			customPathContainer).includePathPattern("/account/**", HttpMethod.POST);
		registry.addInterceptor(matcherInterceptor);
	}
}
