package numble.bankingapi.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.config.BeanIds;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import numble.bankingapi.banking.application.AccountApplicationService;
import numble.bankingapi.member.application.MemberApplicationService;
import numble.bankingapi.member.domain.MemberRepository;

@WebMvcTest
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
public class DocumentationTemplate {
	@MockBean
	protected AccountApplicationService accountApplicationService;
	@MockBean
	protected MemberApplicationService memberApplicationService;
	@MockBean
	protected MemberRepository memberRepository;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	protected MockMvc mockMvc;
	protected PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	protected ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	public void setup(RestDocumentationContextProvider restDocumentation) {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(documentationConfiguration(restDocumentation))
			.build();
	}

	protected OperationRequestPreprocessor getDocumentRequest() {
		return Preprocessors.preprocessRequest(
			Preprocessors.modifyUris()
				.scheme("http")
				.host("127.0.0.1")
				.port(8080),
			Preprocessors.prettyPrint()
		);
	}

	protected OperationResponsePreprocessor getDocumentResponse() {
		return Preprocessors.preprocessResponse(Preprocessors.prettyPrint());
	}
}
