package numble.bankingapi.documentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import numble.bankingapi.banking.application.AccountApplicationService;
import numble.bankingapi.idempotent.domain.IdempotentRequestHistoryService;
import numble.bankingapi.member.application.MemberApplicationService;
import numble.bankingapi.member.domain.MemberRepository;
import numble.bankingapi.social.domain.SocialNetworkService;
import numble.bankingapi.util.matcher.CustomPathContainer;

@WebMvcTest
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
class DocumentationTemplate {
	@MockBean
	protected SocialNetworkService socialNetworkService;
	@MockBean
	protected AccountApplicationService accountApplicationService;
	@MockBean
	protected MemberApplicationService memberApplicationService;
	@MockBean
	protected MemberRepository memberRepository;
	@MockBean
	protected IdempotentRequestHistoryService idempotentRequestHistoryService;
	@MockBean
	protected CustomPathContainer customPathContainer;
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		when(customPathContainer.notIncludedPath(any())).thenReturn(true);
	}

	protected static final User USER = new User("member@email.com", "password",
		List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));

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
