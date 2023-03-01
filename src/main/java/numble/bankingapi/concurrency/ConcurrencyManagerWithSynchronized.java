package numble.bankingapi.concurrency;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcurrencyManagerWithSynchronized implements ConcurrencyManager {
	@Override
	public void executeWithLock(String lockName, Runnable runnable) {
		log.debug("Start Concurrency Control : {}", lockName);
		synchronized (this) {
			runnable.run();
		}
		log.debug("End Concurrency Control : {}", lockName);
	}
}
