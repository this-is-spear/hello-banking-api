package numble.bankingapi.acceptance;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@Disabled
class FriendAcceptanceTest extends AcceptanceTest {

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
		Long memberId = Long.parseLong(loadData.get(MEMBER_ID));
		Long adminId = Long.parseLong(loadData.get(ADMIN_ID));

		ResultActions 친구_신청 = 친구_신청(adminId, 이메일, 비밀번호);
		친구_신청.andExpect(status().isOk());

		// given : action 상대방
		ResultActions 친구_신청_목록_조회 = 친구_신청_목록_조회(어드민이메일, 비밀번호);
		친구_신청_목록_조회.andExpect(status().isOk());
		친구_신청_목록_조회.andExpect(jsonPath("$..userId").value(memberId));

		// when : action 상대방
		ResultActions 친구_승인 = 친구_추가_승인(memberId, 어드민이메일, 비밀번호);
		친구_승인.andExpect(status().isOk());

		// then : action 상대방
		ResultActions 친구_목록_조회 = 친구_목록_조회(어드민이메일, 비밀번호);
		친구_목록_조회.andExpect(status().isOk());
		친구_목록_조회.andExpect(jsonPath("$..userId").value(memberId));
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
		Long memberId = Long.parseLong(loadData.get(MEMBER_ID));
		Long adminId = Long.parseLong(loadData.get(ADMIN_ID));
		ResultActions 친구_신청 = 친구_신청(adminId, 이메일, 비밀번호);
		친구_신청.andExpect(status().isOk());

		// given : action 상대방
		ResultActions 친구_신청_목록_조회 = 친구_신청_목록_조회(어드민이메일, 비밀번호);
		친구_신청_목록_조회.andExpect(status().isOk());
		친구_신청_목록_조회.andExpect(jsonPath("$..userId", memberId).exists());

		// when : action 상대방
		ResultActions 친구_승인 = 친구_추가_거절(memberId, 어드민이메일, 비밀번호);
		친구_승인.andExpect(status().isOk());

		// then : action 상대방
		ResultActions 친구_목록_조회 = 친구_목록_조회(어드민이메일, 비밀번호);
		친구_목록_조회.andExpect(status().isOk());
		친구_목록_조회.andExpect(jsonPath("$..userId", memberId).doesNotExist());
	}

	/**
	 * @Given : 사용자가 한 번 거절해도
	 * @When : 다시 친구 신청하면
	 * @Then : 승낙할 수 있다.
	 */
	@Test
	@DisplayName("거절한 사용자에게 다시 친구 신청을 보낼 수 있다.")
	void reject() throws Exception {
		// given : action 사용자
		Long memberId = Long.parseLong(loadData.get(MEMBER_ID));
		Long adminId = Long.parseLong(loadData.get(ADMIN_ID));
		ResultActions 친구_신청 = 친구_신청(adminId, 이메일, 비밀번호);
		친구_신청.andExpect(status().isOk());

		// given : action 상대방
		ResultActions 친구_신청_목록_조회 = 친구_신청_목록_조회(어드민이메일, 비밀번호);
		친구_신청_목록_조회.andExpect(status().isOk());
		친구_신청_목록_조회.andExpect(jsonPath("$..userId", memberId).exists());

		// when : action 상대방
		ResultActions 친구_승인 = 친구_추가_거절(memberId, 어드민이메일, 비밀번호);
		친구_승인.andExpect(status().isOk());

		// given : action 사용자
		ResultActions 친구_재신청 = 친구_신청(adminId, 이메일, 비밀번호);
		친구_재신청.andExpect(status().isOk());

		// then : action 상대방
		ResultActions 친구_목록_조회 = 친구_목록_조회(어드민이메일, 비밀번호);
		친구_목록_조회.andExpect(status().isOk());
		친구_목록_조회.andExpect(jsonPath("$..userId").value(memberId));
	}

	private ResultActions 친구_목록_조회(String username, String password) throws Exception {
		return mockMvc.perform(
			get("/member/friends/requests")
				.accept(MediaType.APPLICATION_JSON)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private ResultActions 친구_추가_승인(Long userId, String username, String password) throws Exception {
		return mockMvc.perform(
			post("/member/friends/{userId}/approval", userId)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private ResultActions 친구_추가_거절(Long userId, String username, String password) throws Exception {
		return mockMvc.perform(
			post("/member/friends/{userId}/rejected", userId)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private ResultActions 친구_신청_목록_조회(String username, String password) throws Exception {
		return mockMvc.perform(
			get("/member/friends/requests")
				.accept(MediaType.APPLICATION_JSON)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}

	private ResultActions 친구_신청(Long userId, String username, String password) throws Exception {
		return mockMvc.perform(
			post("/member/friends/{userId}", userId)
				.with(user(username).password(password).roles("MEMBER"))
		);
	}
}
