package numble.bankingapi.documentation;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import numble.bankingapi.social.dto.AskedFriendResponse;
import numble.bankingapi.social.dto.AskedFriendResponses;
import numble.bankingapi.social.dto.FriendResponse;
import numble.bankingapi.social.dto.FriendResponses;

class SocialDocumentation extends DocumentationTemplate {

	private static final String USERNAME = "member@gmail.com";
	private static final String SOMEONE_USERNAME = "member@gmail.com";
	private static final String PASSWORD = "password";
	private static final UsernamePasswordAuthenticationToken MEMBER = new UsernamePasswordAuthenticationToken(USERNAME,
		PASSWORD, List.of(new SimpleGrantedAuthority("MEMBER")));
	private static final UsernamePasswordAuthenticationToken SOMEONE_USER = new UsernamePasswordAuthenticationToken(
		SOMEONE_USERNAME,
		PASSWORD, List.of(new SimpleGrantedAuthority("MEMBER")));

	@Test
	@DisplayName("상대방에게 친구 요청을 보낸다.")
	void askWantToBefriends() throws Exception {
		Long someoneId = 2L;

		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(MEMBER);

		doNothing().when(socialNetworkService).askWantToBefriends(USERNAME, someoneId);
		MockHttpServletRequestBuilder builder = post("/member/friends/{someoneId}", someoneId)
			.with(user(USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(
				document(
					"social/ask",
					getDocumentRequest(),
					getDocumentResponse(),
					pathParameters(parameterWithName("someoneId").description("친구 요청을 받을 사용자"))
				)
			);
	}

	@Test
	@DisplayName("자신에게 온 친구 요청을 승낙한다.")
	void approvalRequest() throws Exception {
		Long requestId = 1L;

		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(SOMEONE_USER);

		doNothing().when(socialNetworkService).approvalRequest(SOMEONE_USERNAME, requestId);
		MockHttpServletRequestBuilder builder = post("/member/friends/{requestId}/approval", requestId)
			.with(user(SOMEONE_USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(
				document(
					"social/approval",
					getDocumentRequest(),
					getDocumentResponse(),
					pathParameters(parameterWithName("requestId").description("친구 요청 정보"))
				)
			);
	}

	@Test
	@DisplayName("자신에게 온 친구 요청을 거절한다.")
	void rejectRequest() throws Exception {
		Long requestId = 1L;

		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(SOMEONE_USER);

		doNothing().when(socialNetworkService).rejectRequest(SOMEONE_USERNAME, requestId);
		MockHttpServletRequestBuilder builder = post("/member/friends/{requestId}/rejected", requestId)
			.with(user(SOMEONE_USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(
				document(
					"social/rejected",
					getDocumentRequest(),
					getDocumentResponse(),
					pathParameters(parameterWithName("requestId").description("친구 요청 정보"))
				)
			);
	}

	@Test
	@DisplayName("자신의 친구 목록을 조회한다.")
	void findFriends() throws Exception {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		FriendResponses friendResponses = new FriendResponses(
			List.of(new FriendResponse(4L, "name", "member@email.com"),
				new FriendResponse(11L, "name113", "member14@email.com"))
		);

		securityContext.setAuthentication(SOMEONE_USER);

		when(socialNetworkService.findFriends(SOMEONE_USERNAME)).thenReturn(friendResponses);
		MockHttpServletRequestBuilder builder = get("/member/friends")
			.with(user(SOMEONE_USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(
				document(
					"social/findFriends",
					getDocumentRequest(),
					getDocumentResponse()
				)
			);
	}

	@Test
	@DisplayName("자신에게 온 친구 요청 목록을 확인한다.")
	void findRequestWandToBeFriend() throws Exception {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(SOMEONE_USER);

		AskedFriendResponses askedFriendResponses = new AskedFriendResponses(
			List.of(new AskedFriendResponse(2L, 13L, "name", "member@gmail.com"),
				new AskedFriendResponse(22L, 133L, "name123", "member123@email.com"))
		);

		when(socialNetworkService.findRequestWandToBeFriend(SOMEONE_USERNAME)).thenReturn(askedFriendResponses);
		MockHttpServletRequestBuilder builder = get("/member/friends/requests")
			.with(user(SOMEONE_USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andDo(
				document(
					"social/askedFriends",
					getDocumentRequest(),
					getDocumentResponse()
				)
			);
	}
}
