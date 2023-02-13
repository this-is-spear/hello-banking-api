package numble.bankingapi.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class HelloDocumentation extends DocumentationTemplate {

	@Test
	void hello() throws Exception {
		MockHttpServletRequestBuilder builder = RestDocumentationRequestBuilders
			.get("/hello");

		mockMvc.perform(builder)
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
