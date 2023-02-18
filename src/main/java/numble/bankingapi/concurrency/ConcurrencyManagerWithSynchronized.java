package numble.bankingapi.concurrency;

import org.springframework.stereotype.Service;

@Service
public class ConcurrencyManagerWithSynchronized implements ConcurrencyManager {
	@Override
	public void executeWithLock(String lockName, Runnable runnable) {
		synchronized (this) {
			runnable.run();
		}
	}
}
