package numble.bankingapi.acceptance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import numble.bankingapi.util.DatabaseCleanup;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BankingAcceptanceTest {

	@Autowired
	MockMvc mockMvc;

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
	void transfer_5000() {

	}

	/**
	 * 잔액이 3천원인 사용자는 상대방에게 5천원을 이체하려하면 실패한다.
	 *
	 * @Given : 사용자는 3천원을 입금하고
	 * @When : 상대방에게 5천원을 이체하려하면
	 * @Then : 400 Status Code 를 반환받는다.
	 */
	@Test
	void transfer_failed() {

	}

	/**
	 * 백만원 있는 사용자가 상대방에게 동시에 천원을 200번 요청하면 잔액에 80만원 남는다.
	 *
	 * @Given : 사용자는 백만원을 입금하고
	 * @When : 상대방에게 동시에 천 원을 200번 요청하면
	 * @Then : 잔액에 80만원 남는다.
	 */
	@Test
	void transfer_concurrency_200_times() {

	}

	/**
	 * 계좌가 비어있는 사용자에게 10명의 사용자가 천원을 100번 씩 입금하면 잔액이 100만원이 된다.
	 *
	 * @Given : 잔액이 없는 사용자에게
	 * @When : 10명의 사용자가 천원을 100번 씩 입금하면
	 * @Then : 잔액이 100만원이 된다..
	 */
	@Test
	void transfer_concurrency_1000_times() {

	}
}
