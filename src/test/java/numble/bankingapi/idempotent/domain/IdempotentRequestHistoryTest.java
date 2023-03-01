package numble.bankingapi.idempotent.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.http.HttpStatus;

class IdempotentRequestHistoryTest {

	@Test
	@DisplayName("멱등성 기록을 저장한다.")
	void createIdempotentRequestHistory() {
		assertDoesNotThrow(
			() -> new IdempotentRequestHistory(UUID.randomUUID().toString(), HttpStatus.OK)
		);
	}

	@NullAndEmptySource
	@ParameterizedTest
	@DisplayName("ID는 비어있을 수 없다.")
	void createIdempotentRequestHistory_IdNotNullAndEmpty(String invalidId) {
		assertThatThrownBy(
			() -> new IdempotentRequestHistory(invalidId, HttpStatus.OK)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@NullSource
	@ParameterizedTest
	@DisplayName("상태는 비어있을 수 없다.")
	void createIdempotentRequestHistory_IdNotNullAndEmpty(HttpStatus invalidStatus) {
		assertThatThrownBy(
			() -> new IdempotentRequestHistory(UUID.randomUUID().toString(), invalidStatus)
		).isInstanceOf(IllegalArgumentException.class);
	}
}
