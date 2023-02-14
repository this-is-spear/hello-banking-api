package numble.bankingapi.util;

import static org.testcontainers.shaded.com.google.common.base.CaseFormat.*;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Profile("test")
public class DatabaseCleanup implements InitializingBean {
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private List<String> tableNames;

	@Override
	public void afterPropertiesSet() {
		tableNames = entityManager.getMetamodel().getEntities().stream()
			.filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
			.map(e -> UPPER_CAMEL.to(LOWER_UNDERSCORE, e.getName()))
			.toList();
	}

	@Transactional
	public void execute() {
		jdbcTemplate.execute("SET foreign_key_checks = 0;");
		for (String tableName : tableNames) {
			jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
		}
		jdbcTemplate.execute("SET foreign_key_checks = 1;");
	}
}
