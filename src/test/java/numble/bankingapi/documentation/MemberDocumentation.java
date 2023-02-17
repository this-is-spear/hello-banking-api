package numble.bankingapi.documentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import numble.bankingapi.member.domain.Member;

public class MemberDocumentation extends DocumentationTemplate {

	@Test
	void register() throws Exception {
		String email = "rjsckdd12@gmail.com";
		String name = "tis";
		String password = "password";

		Map<String, String> params = new HashMap<>();
		params.put("email", email);
		params.put("name", name);
		params.put("password", password);

		when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());
		when(memberRepository.save(any())).thenReturn(new Member(email, name, password));

		MockHttpServletRequestBuilder builder = post("/members/register")
			.with(csrf())
			.with(anonymous())
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(params));

		mockMvc.perform(builder)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(
				document(
					"register",
					getDocumentRequest(),
					getDocumentResponse(),
					pathParameters()
				)
			);
	}
}
