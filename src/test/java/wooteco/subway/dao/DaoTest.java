package wooteco.subway.dao;

import java.sql.Connection;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DaoTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanseAndSetUp() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("setup_test_db.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("dao_test_fixture.sql"));
        }
    }
}
