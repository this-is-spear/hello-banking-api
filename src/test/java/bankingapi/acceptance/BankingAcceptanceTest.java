package bankingapi.acceptance;

import bankingapi.banking.dto.HistoryResponses;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BankingAcceptanceTest extends AcceptanceTest {
	private static final String IDEMPOTENT_KEY = "Idempotency-Key";
	private static final String AMOUNT = "$.balance.amount";
	private static final String ADMIN = "admin";
	private static final long 천원 = 1_000L;
	private static final long 삼천원 = 3_000L;
	private static final long 만원 = 10_000L;
	private static final long 백만원 = 1_000_000L;

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
		var 입금할_돈 = 오천원;
		var 출금할_돈 = 오천원;
		var 나의계좌 = 계좌_정보_조회(MEMBER);

		var 계좌_입금 = 계좌_입금_요청(나의계좌, 입금할_돈, 이메일, 비밀번호);
		계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(입금할_돈));

		// when
		var 계좌_출금 = 계좌_출금_요청(나의계좌, 출금할_돈, 이메일, 비밀번호);
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
		var 입금할_돈 = 만원;
		var 출금할_돈 = 오천원;
		var 나의계좌 = 계좌_정보_조회(MEMBER);
		var 상대방계좌 = 계좌_정보_조회(ADMIN);

		var 계좌_입금 = 계좌_입금_요청(나의계좌, 입금할_돈, 이메일, 비밀번호);
		계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(입금할_돈));

		// when
		var 계좌_이체 = 계좌_이체_요청(나의계좌, 상대방계좌, 출금할_돈, 이메일, 비밀번호);
		계좌_이체.andExpect(status().isOk());

		// then
		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(입금할_돈 - 출금할_돈));

		계좌_조회_요청(상대방계좌, 어드민이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(출금할_돈));
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
		var 입금할_돈 = 삼천원;
		var 출금할_돈 = 오천원;
		var 나의계좌 = 계좌_정보_조회(MEMBER);
		var 상대방계좌 = 계좌_정보_조회(ADMIN);

		var 계좌_입금 = 계좌_입금_요청(나의계좌, 입금할_돈, 이메일, 비밀번호);
		계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(입금할_돈));

		// when
		var 계좌_이체 = 계좌_이체_요청(나의계좌, 상대방계좌, 출금할_돈, 이메일, 비밀번호);

		// then
		계좌_이체.andExpect(status().isBadRequest());
	}

	/**
	 * 백만원 있는 사용자가 상대방에게 동시에 천원을 50번 요청하면 잔액에 95만원 남는다.
	 *
	 * @Given : 사용자는 백만원을 입금하고
	 * @When : 상대방에게 동시에 천 원을 50 번 요청하면
	 * @Then : 잔액이 95만원 이상 남는다.
	 */
	@Test
	void transfer_concurrency_50_times() throws Exception {
		// given
		var 입금할_돈 = 백만원;
		var 출금할_돈 = 천원;
		var 요청_횟수 = 50;
		var 나의계좌 = 계좌_정보_조회(MEMBER);
		var 상대방계좌 = 계좌_정보_조회(ADMIN);

		var 계좌_입금 = 계좌_입금_요청(나의계좌, 입금할_돈, 이메일, 비밀번호);
		계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(
			jsonPath(AMOUNT).value(입금할_돈));

		// when
		계좌_이체_여러번_요청(나의계좌, 상대방계좌, 출금할_돈, 요청_횟수, 이메일, 비밀번호);

		// then
		assertAll(
			() ->
				계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(jsonPath(AMOUNT)
					.value(입금할_돈 - 출금할_돈 * 요청_횟수)),
			() -> 계좌_조회_요청(상대방계좌, 어드민이메일, 비밀번호).andExpect(jsonPath(AMOUNT)
				.value(출금할_돈 * 요청_횟수))
		);
	}

	/**
	 * 서로 이체를 10번한다.
	 *
	 * @Given : 사용자와 상배방은 백만원씩 있고
	 * @When : 서로 만원씩 10번 이체하면서 문제가 생겨도
	 * @Then : 총 합 2백만원이 남는다.
	 */
	@Test
	void transfer_concurrency_10times() throws Exception {
		// given
		var 입금할_돈 = 백만원;
		var 출금할_돈 = 만원;
		var 요청_횟수 = 10;
		var 나의계좌 = 계좌_정보_조회(MEMBER);
		var 상대방계좌 = 계좌_정보_조회(ADMIN);

		var 내_계좌_입금 = 계좌_입금_요청(나의계좌, 입금할_돈, 이메일, 비밀번호);
		내_계좌_입금.andExpect(status().isOk());
		var 상대방_계좌_입금 = 계좌_입금_요청(상대방계좌, 입금할_돈, 어드민이메일, 비밀번호);
		상대방_계좌_입금.andExpect(status().isOk());

		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(jsonPath(AMOUNT).value(입금할_돈));

		계좌_조회_요청(상대방계좌, 어드민이메일, 비밀번호).andExpect(jsonPath(AMOUNT).value(입금할_돈));

		var latch = new CountDownLatch(요청_횟수);

		var executorService = Executors.newFixedThreadPool(4);

		for (int i = 0; i < 요청_횟수; i++) {
			executorService.execute(() -> {
				try {
					계좌_이체_요청(나의계좌, 상대방계좌, 출금할_돈, 이메일, 비밀번호).andExpect(status().isOk());
					계좌_이체_요청(상대방계좌, 나의계좌, 출금할_돈, 어드민이메일, 비밀번호).andExpect(status().isOk());
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		// then
		assertAll(
				() -> assertEquals(getHistoryResponses(나의계좌, 이메일).balance().getAmount()
						+ getHistoryResponses(상대방계좌, 어드민이메일).balance().getAmount(), 백만원 * 2),
				() -> assertThat(getHistoryResponses(상대방계좌, 어드민이메일).historyResponses()).hasSameSizeAs(
						getHistoryResponses(나의계좌, 이메일).historyResponses())
		);
	}


	/**
	 * 입금을 동시에 10번한다.
	 *
	 * @When : 사용자의 계좌에 만원씩 10번 입금할 때
	 * @Then : 요청 횟수 이력에 맞게 잔액이 남는다.
	 */
	@Test
	void deposit_concurrency_10times() throws Exception {
		// when
		var 입금할_돈 = 만원;
		var 요청_횟수 = 10;
		var 나의계좌 = 계좌_정보_조회(MEMBER);

		var latch = new CountDownLatch(요청_횟수);

		var executorService = Executors.newFixedThreadPool(4);

		for (int i = 0; i < 요청_횟수; i++) {
			executorService.execute(() -> {
				try {
					계좌_입금_요청(나의계좌, 입금할_돈, 이메일, 비밀번호).andExpect(status().isOk());
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();


		// then
		var successDepositTime = getHistoryResponses(나의계좌, 이메일).historyResponses().size();
        assertAll(
                () -> 계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(jsonPath(AMOUNT).value(입금할_돈 * successDepositTime)),
                ()-> assertThat( getHistoryResponses(나의계좌, 이메일).balance().getAmount())
                        .isGreaterThan(0L)
                        .isLessThan(10_000L)
        );}


	/**
	 * 출금을 동시에 10번한다.
	 *
	 * @Given : 사용자와 상대방은 백만원씩 있고
	 * @When : 사용자의 계좌에 만원씩 10번 출금하면
	 * @Then : 사용자의 계좌에 구십 만원에서 백만원 사이의 현금이 남는다.
	 */
	@Test
	void withdraw_concurrency_10times() throws Exception {
		// when
		var 입금할_돈 = 백만원;
		var 출금할_돈 = 만원;
		var 요청_횟수 = 10;
		var 나의계좌 = 계좌_정보_조회(MEMBER);

		var 내_계좌_입금 = 계좌_입금_요청(나의계좌, 입금할_돈, 이메일, 비밀번호);
		내_계좌_입금.andExpect(status().isOk());
		계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(jsonPath(AMOUNT).value(입금할_돈));

		var latch = new CountDownLatch(요청_횟수);

		var executorService = Executors.newFixedThreadPool(4);

		for (int i = 0; i < 요청_횟수; i++) {
			executorService.execute(() -> {
				try {
					계좌_출금_요청(나의계좌, 출금할_돈, 이메일, 비밀번호).andExpect(status().isOk());
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		// then
        var succeededDepositTime = getHistoryResponses(나의계좌, 이메일).historyResponses().size() - 1;
        assertAll(
                () -> 계좌_조회_요청(나의계좌, 이메일, 비밀번호).andExpect(jsonPath(AMOUNT).value(입금할_돈 - 출금할_돈 * succeededDepositTime)),
                ()-> assertThat( getHistoryResponses(나의계좌, 이메일).balance().getAmount())
                        .isGreaterThan(900_000L)
                        .isLessThan(1_000_000L)
        );
	}

	private void 계좌_이체_여러번_요청(String fromAccountNumber, String toAccountNumber, long transferMoney, int times,
		String username, String password) throws InterruptedException {
		var 스레드_개수 = 6;
		var latch = new CountDownLatch(times);

		var executorService = Executors.newFixedThreadPool(스레드_개수);

		for (int i = 0; i < times; i++) {
			executorService.execute(() -> {
				try {
					계좌_이체_요청(fromAccountNumber, toAccountNumber, transferMoney, username, password);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
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
				.header(IDEMPOTENT_KEY, UUID.randomUUID().toString())
				.content(objectMapper.writeValueAsString(transferParams))
		).andDo(print());
	}

	private ResultActions 계좌_입금_요청(String accountNumber, long depositMoney, String username, String password)
		throws Exception {
		var depositParams = new HashMap<>();
		depositParams.put("amount", depositMoney);

		return mockMvc.perform(
			post("/account/{accountNumber}/deposit", accountNumber)
				.with(user(username).password(password).roles("MEMBER"))
				.contentType(MediaType.APPLICATION_JSON)
				.header(IDEMPOTENT_KEY, UUID.randomUUID().toString())
				.content(objectMapper.writeValueAsString(depositParams))
		).andDo(print());
	}

	private ResultActions 계좌_출금_요청(String accountNumber, long depositMoney, String username, String password)
		throws Exception {
		var depositParams = new HashMap<>();
		depositParams.put("amount", depositMoney);

		return mockMvc.perform(
			post("/account/{accountNumber}/withdraw", accountNumber)
				.with(user(username).password(password).roles("MEMBER"))
				.contentType(MediaType.APPLICATION_JSON)
				.header(IDEMPOTENT_KEY, UUID.randomUUID().toString())
				.content(objectMapper.writeValueAsString(depositParams))
		).andDo(print());
	}

	private ResultActions 계좌_조회_요청(String accountNumber, String username, String password) throws Exception {
		return mockMvc.perform(
			get("/account/{accountNumber}/history", accountNumber)
				.with(user(username).password(password).roles("MEMBER"))
				.accept(MediaType.APPLICATION_JSON)
		);
	}

	private HistoryResponses getHistoryResponses(String account, String email) throws Exception {
		return objectMapper.readValue(계좌_조회_요청(account, email, 비밀번호).andReturn().getResponse()
				.getContentAsByteArray(), HistoryResponses.class);
	}
}
