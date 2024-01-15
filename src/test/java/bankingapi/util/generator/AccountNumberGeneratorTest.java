package bankingapi.util.generator;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.random.RandomGenerator;

import bankingapi.util.generator.AccountNumberGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

class AccountNumberGeneratorTest {

	@RepeatedTest(100)
	@DisplayName("Java 17에서 추가된 RandomGenerator ints(stream size, min, max)를 지정하면 min 이상 nax 미만의 값이 stream size 개 나옵니다.")
	void getNumberUsingRandomGenerator() {
		var randomGenerator = RandomGenerator.getDefault();

		randomGenerator.ints(13, 0, 10).forEach(
			v -> assertThat(v).isGreaterThanOrEqualTo(0).isLessThan(10)
		);
	}

	@RepeatedTest(100)
	@DisplayName("15 자리의 AccountNumber 를 꺼냅니다.")
	void getAccountNumber() {
		assertDoesNotThrow(
			AccountNumberGenerator::generate
		);
	}
}
