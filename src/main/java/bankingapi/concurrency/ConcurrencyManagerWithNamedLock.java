package bankingapi.concurrency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConcurrencyManagerWithNamedLock implements ConcurrencyManager {
    private static final String GET_LOCK = "SELECT GET_LOCK(?, ?)";
    private static final String RELEASE_SESSION_LOCKS = "SELECT RELEASE_ALL_LOCKS()";
    private static final String RELEASE_LOCK = "SELECT RELEASE_LOCK(?)";
    private static final String EXCEPTION_MESSAGE = "LOCK 을 수행하는 중에 오류가 발생하였습니다.";
    private static final int TIMEOUT_SECONDS = 5;
    private static final String EMPTY_RESULT_MESSAGE = "USER LEVEL LOCK 쿼리 결과 값이 NULL 입니다. type = [{}], userLockName : [{}]";
    private static final String INVALID_RESULT_MESSAGE = "USER LEVEL LOCK 쿼리 결과 값이 0 입니다. type = [{}], result : [{}] userLockName : [{}]";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DataSource userLoackDataSource;

    @Override
    public void executeWithLock(String lockName1, String lockName2, Runnable runnable) {
        try (var connection = userLoackDataSource.getConnection()) {
            try {
                log.debug("start getLock=[{}], timeoutSeconds : [{}], connection=[{}]", getMultiLockName(lockName1, lockName2), TIMEOUT_SECONDS, connection);
                getLock(connection, getMultiLockName(lockName1, lockName2));
                try {
                    log.debug("start getLock=[{}], timeoutSeconds : [{}], connection=[{}]", lockName1, TIMEOUT_SECONDS, connection);
                    getLock(connection, lockName1);
                    try {
                        log.debug("start getLock=[{}], timeoutSeconds : [{}], connection=[{}]", lockName2, TIMEOUT_SECONDS, connection);
                        getLock(connection, lockName2);
                        runnable.run();
                    } finally {
                        log.debug("start releaseLock=[{}], connection=[{}]", lockName2, connection);
                        releaseLock(connection, lockName2);
                    }
                }finally {
                    log.debug("start releaseLock=[{}], connection=[{}]", lockName1, connection);
                    releaseLock(connection, lockName1);
                }
            } finally {
                log.debug("start releaseLock=[{}], connection=[{}]", getMultiLockName(lockName1, lockName2), connection);
                releaseLock(connection, getMultiLockName(lockName1, lockName2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeWithLock(String lockName, Runnable runnable) {
        try (var connection = userLoackDataSource.getConnection()) {
            log.info("start getLock=[{}], timeoutSeconds : [{}], connection=[{}]", lockName, TIMEOUT_SECONDS, connection);
            getLock(connection, lockName);
            try {
                runnable.run();
            } finally {
                log.info("start releaseLock, connection=[{}]", connection);
                releaseLock(connection, lockName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void getLock(Connection connection, String userLockName) {
        try (var preparedStatement = connection.prepareStatement(GET_LOCK)) {
            preparedStatement.setString(1, userLockName);
            preparedStatement.setInt(2, TIMEOUT_SECONDS);
            var resultSet = preparedStatement.executeQuery();
            validateResult(resultSet, userLockName, "GetLock");
        } catch (SQLException e) {
            log.error("GetLock_{} : {}", userLockName, e.getMessage());
            throw new IllegalStateException("SQL Exception");
        }
    }
    private void releaseLock(Connection connection, String userLockName) {
        try (var preparedStatement = connection.prepareStatement(RELEASE_LOCK)) {
            preparedStatement.setString(1, userLockName);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            log.error("Release Lock : {}", e.getMessage());
            throw new IllegalStateException("SQL Exception");
        }
    }
    private void releaseSessionLocks(Connection connection) {
        try (var preparedStatement = connection.prepareStatement(RELEASE_SESSION_LOCKS)) {
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            log.error("ReleaseSessionLocks : {}", e.getMessage());
            throw new IllegalStateException("SQL Exception");
        }
    }

    private void releaseSessionLocks() {
        Map<String, Object> params = new HashMap<>();
        namedParameterJdbcTemplate.queryForObject(RELEASE_SESSION_LOCKS, params, Integer.class);
    }

    private void validateResult(ResultSet resultSet, String userLockName, String type) throws SQLException {
        if (!resultSet.next()) {
            log.error(EMPTY_RESULT_MESSAGE, type, userLockName);
            throw new ConcurrencyFailureException(EXCEPTION_MESSAGE);
        }
        int result = resultSet.getInt(1);
        if (result == 0) {
            log.error(INVALID_RESULT_MESSAGE, type, result, userLockName);
            throw new ConcurrencyFailureException(EXCEPTION_MESSAGE);
        }
    }

    private static String getMultiLockName(String lockName1, String lockName2) {
        return Stream.of(lockName1, lockName2).sorted().reduce((a, b) -> a + b).get();
    }
}
