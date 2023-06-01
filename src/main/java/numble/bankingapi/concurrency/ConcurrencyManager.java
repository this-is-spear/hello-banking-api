package numble.bankingapi.concurrency;

public interface ConcurrencyManager {
	void executeWithLock(String lockName, String number, Runnable runnable);
}
