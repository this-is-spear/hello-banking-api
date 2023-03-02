package numble.bankingapi.acceptance;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;

import numble.bankingapi.social.dto.AskedFriendResponses;

class FriendAcceptanceTest extends AcceptanceTest {

	private static final String FROM_USER_ID = "$.askedFriendResponses..fromUserId";
	private static final String MEMBER_ID = "memberId";
	private static final String ADMIN_ID = "adminId";

	/**
	 * @Given : 친구 신청을 받은 사용자가
	 * @Given : 친구 신청 목록을 조회해
	 * @When : 해당 신청을 승인하면
	 * @Then : 친구 목록에 추가 된다.
	 */
	@Test
	@DisplayName("친구 신청을 받은 사용자가 승인을 하면 친구 목록에 추가가 된다.")
	void approval_and_getFriends() throws Exception {
		// given : action 사용자
		var memberId = Long.parseLong(loadData.get(MEMBER_ID));
		var adminId = Long.parseLong(loadData.get(ADMIN_ID));

		var 친구_신청 = 친구_신청(adminId, 이메일, 비밀번호);
		친구_신청.andExpect(status().isOk());

		// given : action 상대방
		var 친구_신청_목록_조회 = 친구_신청_목록_조회(어드민이메일, 비밀번호);
		친구_신청_목록_조회.andExpect(status().isOk());
		친구_신청_목록_조회.andExpect(jsonPath(FROM_USER_ID, memberId).exists());

		var requestId = getRequestId(memberId, 친구_신청_목록_조회);

		// when : action 상대방
		var 친구_승인 = 친구_추가_승인(requestId, 어드민이메일, 비밀번호);
		친구_승인.andExpect(status().isOk());

		// then : action 상대방
		var 친구_목록_조회 = 친구_목록_조회(어드민이메일, 비밀번호);
		친구_목록_조회.andExpect(status().isOk());
		친구_신청_목록_조회.andExpect(jsonPath(FROM_USER_ID, memberId).exists());
	}

	/**
	 * @Given : 친구 신청을 받은 사용자가
	 * @Given : 친구 신청 목록을 조회해
	 * @When : 해당 신청을 거절하면
	 * @Then : 친구 목록에 추가되지 않는다.
	 */
	@Test
	@DisplayName("친구 요청을 받은 사용자가 거절하면 친구 목록에서 볼 수 없다.")
	void reject_and_getFriends() throws Exception {
		// given : action 사용자
		var memberId = Long.parseLong(loadData.get(MEMBER_ID));
		var adminId = Long.parseLong(loadData.get(ADMIN_ID));
		var 친구_신청 = 친구_신청(adminId, 이메일, 비밀번호);
		친구_신청.andExpect(status().isOk());

		// given : action 상대방
		var 친구_신청_목록_조회 = 친구_신청_목록_조회(어드민이메일, 비밀번호);
		친구_신청_목록_조회.andExpect(status().isOk());
		친구_신청_목록_조회.andExpect(jsonPath(FROM_USER_ID, memberId).exists());

		var requestId = getRequestId(memberId, 친구_신청_목록_조회);

		// when : action 상대방
		var 친구_승인 = 친구_추가_거절(requestId, 어드민이메일, 비밀번호);
		친구_승인.andExpect(status().isOk());

		// then : action 상대방
		var 친구_목록_조회 = 친구_신청_목록_조회(어드민이메일, 비밀번호);
		친구_목록_조회.andExpect(status().isOk());
		친구_목록_조회.andExpect(jsonPath(FROM_USER_ID, memberId).doesNotExist());
	}

	/**
	 * @Given : 사용자가 한 번 거절해도
	 * @When : 다시 친구 신청하면
	 * @Then : 승낙할 수 있다.
	 */
	@Test
	@DisplayName("거절한 사용자에게 다시 친구 신청을 보낼 수 있다.")
	void reject_requestAgain() throws Exception {
		// given : action 사용자
		var memberId = Long.parseLong(loadData.get(MEMBER_ID));
		var adminId = Long.parseLong(loadData.get(ADMIN_ID));
		var 친구_신청 = 친구_신청(adminId, 이메일, 비밀번호);
		친구_신청.andExpect(status().isOk());

		// given : action 상대방
		var 친구_신청_목록_조회 = 친구_신청_목록_조회(어드민이메일, 비밀번호);
		친구_신청_목록_조회.andExpect(status().isOk());
		친구_신청_목록_조회.andExpect(jsonPath(FROM_USER_ID, memberId).exists());

		var requestId = getRequestId(memberId, 친구_신청_목록_조회);

		// when : action 상대방
		var 친구_승인 = 친구_추가_거절(requestId, 어드민이메일, 비밀번호);
		친구_승인.andExpect(status().isOk());

		// given : action 사용자
		var 친구_재신청 = 친구_신청(adminId, 이메일, 비밀번호);
		친구_재신청.andExpect(status().isOk());

		// then : action 상대방
		var 친구_목록_조회 = 친구_목록_조회(어드민이메일, 비밀번호);
		친구_목록_조회.andExpect(status().isOk());
		친구_신청_목록_조회.andExpect(jsonPath(FROM_USER_ID, memberId).exists());
	}

	private ResultActions 친구_목록_조회(String username, String password) throws Exception {
		return mockMvc.perform(
			get("/members/friends")
				.accept(MediaType.APPLICATION_JSON)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private ResultActions 친구_추가_승인(Long requestId, String username, String password) throws Exception {
		return mockMvc.perform(
			post("/members/friends/{requestId}/approval", requestId)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private ResultActions 친구_추가_거절(Long requestId, String username, String password) throws Exception {
		return mockMvc.perform(
			post("/members/friends/{requestId}/rejected", requestId)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private ResultActions 친구_신청_목록_조회(String username, String password) throws Exception {
		return mockMvc.perform(
			get("/members/friends/requests")
				.accept(MediaType.APPLICATION_JSON)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private ResultActions 친구_신청(Long userId, String username, String password) throws Exception {
		return mockMvc.perform(
			post("/members/friends/{userId}", userId)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private Long getRequestId(Long memberId, ResultActions resultActions) throws
		JsonProcessingException, UnsupportedEncodingException {
		var friendResponses = objectMapper.readValue(
			resultActions.andReturn().getResponse().getContentAsString(), AskedFriendResponses.class);

		return friendResponses.askedFriendResponses()
			.stream()
			.filter(askedFriendResponse ->
				askedFriendResponse.fromUserId().equals(memberId))
			.findFirst()
			.get().requestId();
	}
}
