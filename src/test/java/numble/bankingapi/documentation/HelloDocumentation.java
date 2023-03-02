package numble.bankingapi.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import numble.bankingapi.util.WithMockMember;

class HelloDocumentation extends DocumentationTemplate {

	@Test
	@WithMockMember
	void hello() throws Exception {
		var builder = RestDocumentationRequestBuilders
			.get("/hello")
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(
				document(
					"hello",
					getDocumentRequest(),
					getDocumentResponse(),
					pathParameters()
				)
			);
	}
}
