package numble.bankingapi.idempotent.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
class IdempotentRequestHistoryServiceTest {

	@Autowired
	private IdempotentRequestHistoryService idempotentRequestHistoryService;

	@Autowired
	private IdempotentRequestRepository idempotentRequestRepository;

	@Test
	@DisplayName("요청에 대한 응답을 저장한다.")
	void saveIdempotentRequestHistory() {
		assertDoesNotThrow(
			() -> idempotentRequestHistoryService.save(
				new IdempotentRequestHistory(UUID.randomUUID().toString(), HttpStatus.OK))
		);
	}

	@Test
	@DisplayName("응답 정보가 존재하지 않으면 true를 반환한다.")
	void isPresentIdempotentRequestHistory() {
		var id = UUID.randomUUID().toString();
		idempotentRequestRepository.save(new IdempotentRequestHistory(id, HttpStatus.OK));
		var actual = assertDoesNotThrow(
			() -> idempotentRequestHistoryService.isPresent(id)
		);
		assertThat(actual).isTrue();
	}

	@Test
	@DisplayName("응답 정보가 존재하지 않으면 false를 반환한다.")
	void isNotPresentIdempotentRequestHistory() {
		var 존재하지않는_데이터 = UUID.randomUUID().toString();
		var actual = assertDoesNotThrow(
			() -> idempotentRequestHistoryService.isPresent(존재하지않는_데이터)
		);
		assertThat(actual).isFalse();
	}

	@Test
	@DisplayName("응답 정보를 조회한다.")
	void getIdempotentRequestHistory() {
		var id = UUID.randomUUID().toString();
		HttpStatus status = HttpStatus.OK;

		idempotentRequestRepository.save(new IdempotentRequestHistory(id, status));
		IdempotentRequestHistory history = assertDoesNotThrow(
			() -> idempotentRequestHistoryService.getIdempotentRequestHistory(id)
		);
		assertThat(history.getResponseStatus()).isEqualTo(status);
	}

	@Test
	@DisplayName("응답 정보가 없는 경우 예외가 발생한다.")
	void getIdempotentRequestHistory_notExist() {
		var 존재하지않는_데이터 = UUID.randomUUID().toString();
		assertThatThrownBy(
			() -> idempotentRequestHistoryService.getIdempotentRequestHistory(존재하지않는_데이터)
		).isInstanceOf(IllegalArgumentException.class);
	}

}
