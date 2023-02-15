package numble.bankingapi.concurrency;

public interface ConcurrencyManager {
	void executeWithLock(String namedLockName, Runnable runnable);
}
