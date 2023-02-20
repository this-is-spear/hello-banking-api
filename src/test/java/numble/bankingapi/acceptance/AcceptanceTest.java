package numble.bankingapi.acceptance;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import numble.bankingapi.DataLoader;
import numble.bankingapi.util.DatabaseCleanup;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest {
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ObjectMapper objectMapper;

	protected Map<String, String> loadData;
	protected static final long 천원 = 1_000L;
	protected static final long 삼천원 = 3_000L;
	protected static final long 오천원 = 5_000L;
	protected static final long 만원 = 10_000L;
	protected static final long 백만원 = 1_000_000L;
	protected static final String 이메일 = "member@email.com";
	protected static final String MEMBER = "member";
	protected static final String MEMBER_ID = "memberId";
	protected static final String 어드민이메일 = "admin@gmail.com";
	protected static final String ADMIN = "admin";
	protected static final String ADMIN_ID = "adminId";
	protected static final String 비밀번호 = "password";
	protected static final String AMOUNT = "$.balance.amount";
	@Autowired
	private DatabaseCleanup databaseCleanup;
	@Autowired
	private DataLoader dataLoader;

	@BeforeEach
	public void setUp() {
		databaseCleanup.execute();
		loadData = dataLoader.loadData();
	}
}
