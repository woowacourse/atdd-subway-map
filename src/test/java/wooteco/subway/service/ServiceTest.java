package wooteco.subway.service;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void reset() {
        jdbcTemplate.execute("DELETE FROM section");
        jdbcTemplate.execute("DELETE FROM station");
        jdbcTemplate.execute("DELETE FROM line");
    }
}
