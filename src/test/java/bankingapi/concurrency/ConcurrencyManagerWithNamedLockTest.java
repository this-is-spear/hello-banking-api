package bankingapi.concurrency;

import static org.assertj.core.api.Assertions.*;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.random.RandomGenerator;

import bankingapi.concurrency.ConcurrencyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bankingapi.banking.domain.Account;
import bankingapi.banking.domain.Money;
import bankingapi.util.generator.AccountNumberGenerator;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ConcurrencyManagerWithNamedLockTest {
	private static final int NUMBER_OF_THREADS = 10;
	private static final int POLL_SIZE = 10;
	private static final Money ONE = new Money(1);
	@Autowired
	private ConcurrencyManager concurrencyManager;
	private CountDownLatch latch;

	@BeforeEach
	void setUp() {
		latch = new CountDownLatch(NUMBER_OF_THREADS);
	}

	@Test
	@DisplayName("멀티 스레드 환경에서 동시성을 제어하지 않으면 원자성이 보장되지 않는다.")
	void calculateAtSameTime_notControllingConcurrency() throws InterruptedException {
		var service = Executors.newFixedThreadPool(POLL_SIZE);
		var account = Account.builder()
			.accountNumber(AccountNumberGenerator.generate())
			.balance(Money.zero())
			.userId(2L)
			.build();

		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			service.execute(() -> {
				account.deposit(new Money(1));
				latch.countDown();
			});
		}

		latch.await();
		assertThat((int)account.getBalance().getAmount()).isLessThanOrEqualTo(NUMBER_OF_THREADS);
	}

	@Test
	@DisplayName("멀티 스레드 환경에서 동시성을 제어해 원자성을 보장한다.")
	void calculateAtSameTime_controllingConcurrency() throws InterruptedException {
		var service = Executors.newFixedThreadPool(POLL_SIZE);
		var account = Account.builder()
			.accountNumber(AccountNumberGenerator.generate())
			.balance(Money.zero())
			.userId(2L)
			.build();

		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			service.execute(() -> {
				try {
					Thread.sleep(RandomGenerator.getDefault().nextInt(0, 50));
					concurrencyManager.executeWithLock("lock1", "lock2", () -> account.deposit(ONE));
				} catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		assertThat((int)account.getBalance().getAmount()).isEqualTo(NUMBER_OF_THREADS);
	}
}
