package numble.bankingapi.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Scope(value = "prototype")
public class CustomPathContainer {
	private final List<RequestMatcher> includePathPattern;
	private final List<RequestMatcher> excludePathPattern;

	public CustomPathContainer() {
		this.includePathPattern = new ArrayList<>();
		this.excludePathPattern = new ArrayList<>();
	}

	public boolean notIncludedPath(HttpServletRequest request) {
		boolean excludePattern = excludePathPattern.stream()
			.anyMatch(matcher -> matcher.matches(request));

		boolean includePattern = includePathPattern.stream()
			.anyMatch(matcher -> matcher.matches(request));

		return excludePattern || !includePattern;
	}

	public void includePathPattern(String targetPath, HttpMethod method) {
		this.includePathPattern.add(AntPathRequestMatcher.antMatcher(method, targetPath));
	}

	public void excludePathPattern(String targetPath, HttpMethod method) {
		this.excludePathPattern.add(AntPathRequestMatcher.antMatcher(method, targetPath));
	}
}
