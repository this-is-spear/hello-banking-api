package numble.bankingapi.concurrency;

public interface ConcurrencyManager {
	void executeWithLock(String lockName, Runnable runnable);
}
