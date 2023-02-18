package numble.bankingapi.acceptance;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import numble.bankingapi.DataLoader;
import numble.bankingapi.util.DatabaseCleanup;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankingAcceptanceTest {

	private static final String 이메일 = "member@email.com";
	private static final String 비밀번호 = "password";
	private static final String AMOUNT = "$.balance.amount";
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private DatabaseCleanup databaseCleanup;
	@Autowired
	private DataLoader dataLoader;
	private Map<String, String> accountNumber;

	@BeforeEach
	public void setUp() {
		databaseCleanup.execute();
		accountNumber = dataLoader.loadData();
	}

	/**
	 * 사용자는 5천원을 입금하고 5천원을 출금하면 잔액이 0원 남는다.
	 *
	 * @Given : 사용자는 5천원을 입금하고
	 * @When : 5천원을 출금하면
	 * @Then : 잔액이 0원 남는다.
	 */
	@Test
	void deposit_and_withdraw_5000() throws Exception {
		// given
		String 나의계좌 = accountNumber.get("member");

		long 입금할_돈 = 5_000L;
		ResultActions 계좌_입금 = 계좌_입금_요청(나의계좌, 입금할_돈, 이메일, 비밀번호);
		계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(입금할_돈));

		// when
		long 출금할_돈 = 5_000L;
		ResultActions 계좌_출금 = 계좌_출금_요청(나의계좌, 출금할_돈, 이메일, 비밀번호);
		계좌_출금.andExpect(status().isOk());

		// then
		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(입금할_돈 - 출금할_돈));
	}

	/**
	 * 잔액이 만원인 사용자는 상대방에게 5천원을 이체하면 잔액이 5천원 남는다.
	 *
	 * @Given : 사용자는 만원을 입금하고
	 * @When : 상대방에게 5천원을 이체하면
	 * @Then : 잔액이 5천원 남는다.
	 */
	@Test
	void transfer_5000() throws Exception {
		// given
		String 나의계좌 = accountNumber.get("member");
		String 상대방계좌 = accountNumber.get("admin");

		long depositMoney = 10_000L;
		ResultActions 계좌_입금 = 계좌_입금_요청(나의계좌, depositMoney, 이메일, 비밀번호);
		계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(depositMoney));

		// when
		long transferMoney = 5_000L;
		ResultActions 계좌_이체 = 계좌_이체_요청(나의계좌, 상대방계좌, transferMoney, 이메일, 비밀번호);
		계좌_이체.andExpect(status().isOk());

		// then
		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(depositMoney - transferMoney));

		계좌_조회_요청(상대방계좌, "admin@gmail.com", "password").andExpect(
			jsonPath(AMOUNT).value(transferMoney));
	}

	/**
	 * 잔액이 3천원인 사용자는 상대방에게 5천원을 이체하려하면 실패한다.
	 *
	 * @Given : 사용자는 3천원을 입금하고
	 * @When : 상대방에게 5천원을 이체하려하면
	 * @Then : 400 Status Code 를 반환받는다.
	 */
	@Test
	void transfer_failed() throws Exception {
		// given
		String 나의계좌 = accountNumber.get("member");
		String 상대방계좌 = accountNumber.get("admin");

		long depositMoney = 3_000L;
		ResultActions 계좌_입금 = 계좌_입금_요청(나의계좌, depositMoney, 이메일, 비밀번호);
		계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(depositMoney));

		// when
		long transferMoney = 5_000L;
		ResultActions 계좌_이체 = 계좌_이체_요청(나의계좌, 상대방계좌, transferMoney, 이메일, 비밀번호);

		// then
		계좌_이체.andExpect(status().isBadRequest());
	}

	/**
	 * 백만원 있는 사용자가 상대방에게 동시에 천원을 200번 요청하면 잔액에 80만원 남는다.
	 *
	 * @Given : 사용자는 백만원을 입금하고
	 * @When : 상대방에게 동시에 천 원을 200번 요청하면
	 * @Then : 잔액에 80만원 남는다.
	 */
	@Test
	void transfer_concurrency_200_times() throws Exception {
		// given
		String 나의계좌 = accountNumber.get("member");
		String 상대방계좌 = accountNumber.get("admin");

		long depositMoney = 1_000_000L;
		ResultActions 계좌_입금 = 계좌_입금_요청(나의계좌, depositMoney, 이메일, 비밀번호);
		계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(depositMoney));

		// when
		long transferMoney = 1_000L;
		int times = 200;
		transferManyTimes(나의계좌, 상대방계좌, transferMoney, times, 이메일, 비밀번호);

		// then
		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(depositMoney - transferMoney * times));
	}

	private void transferManyTimes(String fromAccountNumber, String toAccountNumber, long transferMoney, int times,
		String username, String password) throws InterruptedException {
		int threads = 1;
		CountDownLatch latch = new CountDownLatch(times);

		ExecutorService executorService = Executors.newFixedThreadPool(threads);

		for (int i = 0; i < times; i++) {
			executorService.execute(() -> {
				try {
					계좌_이체_요청(fromAccountNumber, toAccountNumber, transferMoney, username, password);
					latch.countDown();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
		latch.await();
	}

	private ResultActions 계좌_이체_요청(String fromAccountNumber, String toAccountNumber, long transferMoney,
		String username, String password) throws Exception {
		Map<String, Object> transferParams = new HashMap<>();
		transferParams.put("toAccountNumber", toAccountNumber);
		transferParams.put("amount", transferMoney);

		return mockMvc.perform(
			post("/account/{accountNumber}/transfer", fromAccountNumber)
				.with(user(username).password(password).roles("MEMBER"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferParams))
		);
	}

	private ResultActions 계좌_입금_요청(String accountNumber, long depositMoney, String username, String password)
		throws Exception {
		Map<String, Object> depositParams = new HashMap<>();
		depositParams.put("amount", depositMoney);

		return mockMvc.perform(
			post("/account/{accountNumber}/deposit", accountNumber)
				.with(user(username).password(password).roles("MEMBER"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(depositParams))
		);
	}

	private ResultActions 계좌_출금_요청(String accountNumber, long depositMoney, String username, String password)
		throws Exception {
		Map<String, Object> depositParams = new HashMap<>();
		depositParams.put("amount", depositMoney);

		return mockMvc.perform(
			post("/account/{accountNumber}/withdraw", accountNumber)
				.with(user(username).password(password).roles("MEMBER"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(depositParams))
		);
	}

	private ResultActions 계좌_조회_요청(String accountNumber, String username, String password) throws Exception {
		return mockMvc.perform(
			get("/account/{accountNumber}/history", accountNumber)
				.with(user(username).password(password).roles("MEMBER"))
				.accept(MediaType.APPLICATION_JSON)
		);
	}

}
