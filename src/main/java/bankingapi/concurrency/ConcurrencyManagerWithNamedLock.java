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
    private final DataSource dataSource;

    @Override
    public void executeWithLock(String lockName1, String lockName2, Runnable runnable) {
        try(var connection = dataSource.getConnection()) {
            getLock(connection, getMultiLockName(lockName1, lockName2));
            getLock(connection, lockName1);
            getLock(connection, lockName2);
            runnable.run();
            releaseLock(connection, lockName2);
            releaseLock(connection, lockName1);
            releaseLock(connection, getMultiLockName(lockName1, lockName2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            releaseSessionLocks();
        }
    }

    @Override
    public void executeWithLock(String lockName, Runnable runnable) {
        try(var connection = dataSource.getConnection()) {
            getLock(connection, lockName);
            runnable.run();
            releaseLock(connection, lockName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            releaseSessionLocks();
        }
    }

    private void releaseLock(Connection connection, String lockName) {
        try (var preparedStatement = connection.prepareStatement(RELEASE_LOCK)) {
            preparedStatement.setString(1, lockName);
            var resultSet = preparedStatement.executeQuery();
            validateResult(resultSet, lockName, "ReleaseLock");
        } catch (SQLException e) {
            log.error("ReleaseLock_{} : {}", lockName, e.getMessage());
            throw new IllegalStateException("SQL Exception");
        }
    }

    private void getLock(Connection connection, String userLockName) {
        try (var preparedStatement = connection.prepareStatement(GET_LOCK)) {
            preparedStatement.setString(1, userLockName);
            preparedStatement.setInt(2, TIMEOUT_SECONDS);

            synchronized (this) {
                var resultSet = preparedStatement.executeQuery();
                validateResult(resultSet, userLockName, "GetLock");
            }

        } catch (SQLException e) {
            log.error("GetLock_{} : {}", userLockName, e.getMessage());
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
