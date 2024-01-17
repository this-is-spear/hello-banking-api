package bankingapi.documentation;

import static bankingapi.fixture.AccountFixture.*;
import static bankingapi.fixture.DocumentationFixture.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import bankingapi.banking.domain.AccountNumber;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import bankingapi.banking.dto.TransferCommand;
import bankingapi.util.WithMockMember;

class AccountDocumentation extends DocumentationTemplate {
	private static final String IDEMPOTENT_KEY = "Idempotency-Key";

	@Test
	@WithMockMember
	void getHistory() throws Exception {
		when(accountApplicationService.getHistory(USER.getUsername(), 계좌_번호))
			.thenReturn(계좌_내역);
		mockMvc.perform(
				get("/accounts/{accountNumber}/history", 계좌_번호)
					.with(csrf())
			).andExpect(status().isOk())
			.andDo(document(
				"history",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}

	@Test
	@WithMockMember
	void deposit() throws Exception {
		doNothing().when(accountApplicationService).deposit(USER.getUsername(), 계좌_번호, 이만원);
		mockMvc.perform(
				post("/accounts/{accountNumber}/deposit", 계좌_번호)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(이만원))
					.header(IDEMPOTENT_KEY, UUID.randomUUID().toString())
					.with(csrf())
			).andExpect(status().isOk())
			.andDo(document(
				"deposit",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}

	@Test
	@WithMockMember
	void withdraw() throws Exception {
		doNothing().when(accountApplicationService).withdraw(USER.getUsername(), 계좌_번호, 이만원);
		mockMvc.perform(
				post("/accounts/{accountNumber}/withdraw", 계좌_번호)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(이만원))
					.header(IDEMPOTENT_KEY, UUID.randomUUID().toString())
					.with(csrf())
			).andExpect(status().isOk())
			.andDo(document(
				"withdraw",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}

	@Test
	@WithMockMember
	void transfer() throws Exception {
		var command = new TransferCommand(계좌_번호, 이만원);
		doNothing().when(accountApplicationService).transfer(USER.getUsername(), 계좌_번호, command);

		mockMvc.perform(
				post("/accounts/{accountNumber}/transfer", 계좌_번호)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(command))
					.header(IDEMPOTENT_KEY, UUID.randomUUID().toString())
					.with(csrf())
			).andExpect(status().isOk())
			.andDo(document(
				"transfer",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}

	@Test
	@WithMockMember
	void getTargets() throws Exception {
		when(accountApplicationService.getTargets(USER.getUsername())).thenReturn(타겟목록);
		mockMvc.perform(
				get("/accounts/transfer/targets")
					.with(csrf())
			).andExpect(status().isOk())
			.andDo(print())
			.andDo(document(
				"targets",
				getDocumentRequest(),
				getDocumentResponse()
			));
	}

	@Test
	@WithMockMember
	void findAccounts() throws Exception {
		when(accountApplicationService.findAccounts(USER.getUsername())).thenReturn(계좌목록);
		mockMvc.perform(
						get("/accounts")
								.with(csrf())
				).andExpect(status().isOk())
				.andDo(print())
				.andDo(document(
						"accounts",
						getDocumentRequest(),
						getDocumentResponse()
				));
	}

	@Test
	@WithMockMember
	void createAccount() throws Exception {
		when(accountApplicationService.createAccount(USER.getUsername())).thenReturn(new AccountNumber(계좌_번호));
		mockMvc.perform(
						post("/accounts")
								.with(csrf())
				).andExpect(status().isOk())
				.andDo(print())
				.andDo(document(
						"create-account",
						getDocumentRequest(),
						getDocumentResponse()
				));
	}
}
