package numble.bankingapi.documentation;

import static numble.bankingapi.fixture.AccountFixture.*;
import static numble.bankingapi.fixture.DocumentationFixture.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import numble.bankingapi.banking.dto.TransferCommand;

class AccountDocumentation extends DocumentationTemplate {

	@Test
	void getHistory() throws Exception {
		when(accountApplicationService.getHistory(USER.getUsername(), 계좌_번호))
			.thenReturn(계좌_내역);
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(
			new UsernamePasswordAuthenticationToken(USER, USER.getPassword(), USER.getAuthorities()));

		mockMvc.perform(
				get("/account/{accountNumber}/history", 계좌_번호)
					.with(csrf())
					.with(user(USER.getUsername()).roles("MEMBER"))
			).andExpect(status().isOk())
			.andDo(document(
				"history",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}

	@Test
	void deposit() throws Exception {
		doNothing().when(accountApplicationService).deposit(USER.getUsername(), 계좌_번호, 이만원);
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(
			new UsernamePasswordAuthenticationToken(USER, USER.getPassword(), USER.getAuthorities()));

		mockMvc.perform(
				post("/account/{accountNumber}/deposit", 계좌_번호)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(이만원))
					.with(csrf())
					.with(user(USER.getUsername()).roles("MEMBER"))
			).andExpect(status().isOk())
			.andDo(document(
				"deposit",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}

	@Test
	void withdraw() throws Exception {
		doNothing().when(accountApplicationService).withdraw(계좌_번호, 이만원);

		mockMvc.perform(
				post("/account/{accountNumber}/withdraw", 계좌_번호)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(이만원))
					.with(csrf())
					.with(user("user").roles("MEMBER"))
			).andExpect(status().isOk())
			.andDo(document(
				"withdraw",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}

	@Test
	void transfer() throws Exception {
		TransferCommand command = new TransferCommand("123-23434-32-1111", 이만원);

		doNothing().when(accountApplicationService).transfer(계좌_번호, command);

		mockMvc.perform(
				post("/account/{accountNumber}/transfer", 계좌_번호)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(command))
					.with(csrf())
					.with(user("user").roles("MEMBER"))
			).andExpect(status().isOk())
			.andDo(document(
				"transfer",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}

	@Test
	void getTargets() throws Exception {
		when(accountApplicationService.getTargets()).thenReturn(타겟목록);

		mockMvc.perform(
				get("/account/{accountNumber}/transfer/targets", 계좌_번호)
					.with(user("user").roles("MEMBER"))
			).andExpect(status().isOk())
			.andDo(document(
				"targets",
				getDocumentRequest(),
				getDocumentResponse(),
				pathParameters(parameterWithName("accountNumber").description("사용자의 계좌 정보"))
			));
	}
}
