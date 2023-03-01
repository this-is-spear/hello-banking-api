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
	private static final String RELEASE_LOCK = "SELECT RELEASE_LOCK(:userLockName)";
	private static final String EXCEPTION_MESSAGE = "LOCK 을 수행하는 중에 오류가 발생하였습니다.";
	private static final int TIMEOUT_SECONDS = 1;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public void executeWithLock(String lockName, Runnable runnable) {
		try {
			getLock(lockName);
			runnable.run();
		} finally {
			releaseLock(lockName);
		}
	}

	private void getLock(String userLockName) {
		Map<String, Object> params = new HashMap<>();
		params.put("userLockName", userLockName);
		params.put("timeoutSeconds", ConcurrencyManagerWithNamedLock.TIMEOUT_SECONDS);
		Integer result = namedParameterJdbcTemplate.queryForObject(GET_LOCK, params, Integer.class);
		validateResult(result, userLockName, "GetLock");
	}

	private void releaseLock(String userLockName) {
		Map<String, Object> params = new HashMap<>();
		params.put("userLockName", userLockName);
		Integer result = namedParameterJdbcTemplate.queryForObject(RELEASE_LOCK, params, Integer.class);
		validateResult(result, userLockName, "ReleaseLock");
	}

	private void validateResult(Integer result, String userLockName, String type) {
		if (result == null) {
			log.error("USER LEVEL LOCK 쿼리 결과 값이 없습니다. type = [{}], userLockName : [{}]", type, userLockName);
			throw new ConcurrencyFailureException(EXCEPTION_MESSAGE);
		}
		if (result != 1) {
			log.error("USER LEVEL LOCK 쿼리 결과 값이 1이 아닙니다. type = [{}], result : [{}] userLockName : [{}]", type, result,
				userLockName);
			throw new ConcurrencyFailureException(EXCEPTION_MESSAGE);
		}
	}
}
