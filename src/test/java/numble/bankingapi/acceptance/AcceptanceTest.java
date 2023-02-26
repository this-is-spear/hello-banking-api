package numble.bankingapi.acceptance;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import numble.bankingapi.util.DatabaseCleanup;
import numble.bankingapi.util.data.DataLoader;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest {
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ObjectMapper objectMapper;
	protected Map<String, String> loadData;
	protected static final String 이메일 = "member@email.com";
	protected static final String 어드민이메일 = "admin@email.com";
	protected static final String 비밀번호 = "password";
	protected static final String MEMBER = "member";
	protected static final long 오천원 = 5_000L;

	protected String 계좌_정보_조회(String member) {
		return loadData.get(member);
	}

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
