package bankingapi.documentation;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bankingapi.social.dto.AskedFriendResponse;
import bankingapi.social.dto.AskedFriendResponses;
import bankingapi.social.dto.FriendResponse;
import bankingapi.social.dto.FriendResponses;
import bankingapi.util.WithMockMember;

class SocialDocumentation extends DocumentationTemplate {

	private static final String USERNAME = "member@email.com";
	private static final String SOMEONE_USERNAME = "rjsckdd12@gmail.com";
	private static final String PASSWORD = "password";

	@Test
	@WithMockMember
	@DisplayName("상대방에게 친구 요청을 보낸다.")
	void askWantToBefriends() throws Exception {
		var someoneId = 2L;
		doNothing().when(socialNetworkService).askWantToBefriends(USERNAME, someoneId);
		var builder = post("/members/friends/{someoneId}", someoneId)
			.with(user(USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(print())
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
	@WithMockMember
	@DisplayName("자신에게 온 친구 요청을 승낙한다.")
	void approvalRequest() throws Exception {
		var requestId = 1L;
		doNothing().when(socialNetworkService).approvalRequest(SOMEONE_USERNAME, requestId);
		var builder = post("/members/friends/{requestId}/approval", requestId)
			.with(user(SOMEONE_USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(print())
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
	@WithMockMember
	@DisplayName("자신에게 온 친구 요청을 거절한다.")
	void rejectRequest() throws Exception {
		var requestId = 1L;
		doNothing().when(socialNetworkService).rejectRequest(SOMEONE_USERNAME, requestId);
		var builder = post("/members/friends/{requestId}/rejected", requestId)
			.with(user(SOMEONE_USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(print())
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
	@WithMockMember
	@DisplayName("자신의 친구 목록을 조회한다.")
	void findFriends() throws Exception {
		var friendResponses = new FriendResponses(
			List.of(new FriendResponse(4L, "name", "member@email.com"),
				new FriendResponse(11L, "name113", "member14@email.com"))
		);

		when(socialNetworkService.findFriends(SOMEONE_USERNAME)).thenReturn(friendResponses);
		var builder = get("/members/friends")
			.with(user(SOMEONE_USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(print())
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
	@WithMockMember
	@DisplayName("자신에게 온 친구 요청 목록을 확인한다.")
	void findRequestWandToBeFriend() throws Exception {
		var askedFriendResponses = new AskedFriendResponses(
			List.of(new AskedFriendResponse(2L, 13L, "name", "member@gmail.com"),
				new AskedFriendResponse(22L, 133L, "name123", "member123@email.com"))
		);

		when(socialNetworkService.findRequestWandToBeFriend(SOMEONE_USERNAME)).thenReturn(askedFriendResponses);
		var builder = get("/members/friends/requests")
			.with(user(SOMEONE_USERNAME).password(PASSWORD))
			.with(csrf());

		mockMvc.perform(builder)
			.andDo(print())
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
