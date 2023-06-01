package numble.bankingapi.concurrency;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcurrencyManagerWithNamedLock implements ConcurrencyManager {
	private static final String GET_LOCK = "SELECT GET_LOCK(:userLockName, :timeoutSeconds)";
	private static final String RELEASE_SESSION_LOCKS = "SELECT RELEASE_ALL_LOCKS()";
	private static final String EXCEPTION_MESSAGE = "LOCK 을 수행하는 중에 오류가 발생하였습니다.";
	private static final int TIMEOUT_SECONDS = 2;
	private static final String EMPTY_RESULT_MESSAGE = "USER LEVEL LOCK 쿼리 결과 값이 없습니다. type = [{}], userLockName : [{}]";
	private static final String INVALID_RESULT_MESSAGE = "USER LEVEL LOCK 이 존재하지 않습니다. type = [{}], result : [{}] userLockName : [{}]";
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public void executeWithLock(String lockName1, String lockName2, Runnable runnable) {
		try {
			getLock(lockName1);
			getLock(lockName2);
			runnable.run();
		} finally {
			releaseSessionLocks();
		}
	}

	private void getLock(String userLockName) {
		Map<String, Object> params = new HashMap<>();
		params.put("userLockName", userLockName);
		params.put("timeoutSeconds", ConcurrencyManagerWithNamedLock.TIMEOUT_SECONDS);
		Integer result = namedParameterJdbcTemplate.queryForObject(GET_LOCK, params, Integer.class);
		validateResult(result, userLockName, "GetLock");
	}

	private void releaseSessionLocks() {
		Map<String, Object> params = new HashMap<>();
		Integer result = namedParameterJdbcTemplate.queryForObject(RELEASE_SESSION_LOCKS, params, Integer.class);
		validateResult(result, "SESSION", "ReleaseLock");
	}

	private void validateResult(Integer result, String userLockName, String type) {
		if (result == null) {
			log.error(EMPTY_RESULT_MESSAGE, type, userLockName);
			throw new ConcurrencyFailureException(EXCEPTION_MESSAGE);
		}
		if (result == 0) {
			log.error(INVALID_RESULT_MESSAGE, type, result,
				userLockName);
			throw new ConcurrencyFailureException(EXCEPTION_MESSAGE);
		}
	}
}
