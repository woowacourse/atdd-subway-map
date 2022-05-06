package wooteco.subway.dao;

import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@Sql(scripts = {"classpath:setupSchema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:delete.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@JdbcTest
public class DaoImplTest {
}
