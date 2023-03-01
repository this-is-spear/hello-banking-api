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
import numble.bankingapi.member.dto.MemberResponse;
import numble.bankingapi.util.WithMockMember;

class MemberDocumentation extends DocumentationTemplate {

	@Test
	@WithMockMember
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

	@Test
	@WithMockMember
	void me() throws Exception {
		when(memberApplicationService.getMember(USER.getUsername()))
			.thenReturn(new MemberResponse(2L, USER.getUsername()));

		MockHttpServletRequestBuilder builder = get("/members/me")
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(
				document(
					"me",
					getDocumentRequest(),
					getDocumentResponse(),
					pathParameters()
				)
			);
	}
}
