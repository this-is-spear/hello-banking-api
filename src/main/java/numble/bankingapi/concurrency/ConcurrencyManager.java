package numble.bankingapi.concurrency;

public interface ConcurrencyManager {
	void executeWithLock(String lockName1, String lockName2, Runnable runnable);

	void executeWithLock(String lockName, Runnable runnable);
}
