package bankingapi.acceptance;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class IdempotencyAcceptanceTest extends AcceptanceTest {
	/**
	 * 사용자는 5천원을 입금할 때, 멱등성 키가 없으면 예외가 발생한다.
	 */
	@Test
	void deposit_and_withdraw_5000() throws Exception {
		// given
		var 입금할_돈 = 오천원;
		var 나의계좌 = 계좌_정보_조회(MEMBER);

		var depositParams = new HashMap<>();
		depositParams.put("amount", 입금할_돈);

		var 계좌_입금 = mockMvc.perform(
			post("/accounts/{accountNumber}/deposit", 나의계좌)
				.with(user(이메일).password(비밀번호).roles("MEMBER"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(depositParams))
		).andDo(print());

		계좌_입금.andExpect(status().isBadRequest());

	}
}
