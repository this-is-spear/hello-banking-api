package numble.bankingapi.acceptance;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import numble.bankingapi.util.DatabaseCleanup;

@Disabled
@AutoConfigureMockMvc
@ActiveProfiles("acceptance")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankingAcceptanceTest {

	@Autowired
	MockMvc mockMvc;

	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private DatabaseCleanup databaseCleanup;

	@BeforeEach
	public void setUp() {
		databaseCleanup.execute();
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
		String fromAccountNumber = "123-234-543-12";
		String toAccountNumber = "123-234-543-12";

		// given
		long depositMoney = 10_000L;
		ResultActions deposit = deposit(fromAccountNumber, depositMoney);
		deposit.andExpect(status().isOk());

		// when
		long transferMoney = 5_000L;
		ResultActions transfer = transfer(fromAccountNumber, toAccountNumber, transferMoney);
		transfer.andExpect(status().isOk());

		// then
		getAccountHistory(fromAccountNumber).andExpect(jsonPath("$.balance").value(depositMoney - transferMoney));
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
		String fromAccountNumber = "123-234-543-12";
		String toAccountNumber = "123-234-543-12";

		// given
		long depositMoney = 3_000L;
		ResultActions deposit = deposit(fromAccountNumber, depositMoney);
		deposit.andExpect(status().isOk());

		// when
		long transferMoney = 5_000L;
		ResultActions transfer = transfer(fromAccountNumber, toAccountNumber, transferMoney);

		// then
		transfer.andExpect(status().isBadRequest());
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
		String fromAccountNumber = "123-234-543-12";
		String toAccountNumber = "123-234-543-12";

		// given
		long depositMoney = 3_000L;
		ResultActions deposit = deposit(fromAccountNumber, depositMoney);
		deposit.andExpect(status().isOk());

		// when
		long transferMoney = 5_000L;
		transfer200Times(fromAccountNumber, toAccountNumber, transferMoney);

		// then
		getAccountHistory(fromAccountNumber).andExpect(jsonPath("$.balance").value(depositMoney - transferMoney));
	}

	private void transfer200Times(String fromAccountNumber, String toAccountNumber, long transferMoney) {
		int times = 200;
		int threads = 20;
		ExecutorService executorService = Executors.newFixedThreadPool(threads);

		for (int i = 0; i < times; i++) {
			executorService.execute(() -> {
				try {
					transfer(fromAccountNumber, toAccountNumber, transferMoney);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	private ResultActions getAccountHistory(String fromAccountNumber) throws Exception {
		return mockMvc.perform(
			get("/account/{accountNumber}/history", fromAccountNumber)
		);
	}

	private ResultActions transfer(String fromAccountNumber, String toAccountNumber, long transferMoney) throws
		Exception {
		Map<String, Object> transferParams = new HashMap<>();
		transferParams.put("toAccountNumber", toAccountNumber);
		transferParams.put("money", transferMoney);

		return mockMvc.perform(
			post("/account/{accountNumber}/transfer", fromAccountNumber)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(transferParams))
		);
	}

	private ResultActions deposit(String accountNumber, long depositMoney) throws Exception {
		Map<String, Object> depositParams = new HashMap<>();
		depositParams.put("money", depositMoney);

		return mockMvc.perform(
			post("/account/{accountNumber}/deposit", accountNumber)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(depositParams))
		);
	}
}
