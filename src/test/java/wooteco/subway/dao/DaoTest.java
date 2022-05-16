package wooteco.subway.dao;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

@JdbcTest
abstract class DaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    protected StationDao stationDao;

    protected LineDao lineDao;

    protected SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        stationDao = new JdbcStationDao(jdbcTemplate, dataSource);
        sectionDao = new JdbcSectionDao(jdbcTemplate);
        lineDao = new JdbcLineDao(jdbcTemplate, dataSource, sectionDao);
    }
}
