package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

@JdbcTest
public class JdbcSectionDaoTest {
    private JdbcSectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionDao = new JdbcSectionDao(jdbcTemplate);

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS SECTION (\n"
                + "    id BIGINT AUTO_INCREMENT NOT NULL,\n"
                + "    up_station_id BIGINT NOT NULL,\n"
                + "    down_station_id BIGINT NOT NULL,\n"
                + "    distance int NOT NULL,\n"
                + "    PRIMARY KEY(id)\n"
                + ");");

        final Section section = new Section(1L,2L,10);
        sectionDao.save(section);
    }

    @Test
    @DisplayName("지하철 구간을 저장한다.")
    void save() {
        final String sql = "SELECT COUNT(*) FROM SECTION";
        final int expected = 1;

        final int actual = jdbcTemplate.queryForObject(sql, Integer.class);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("지하철 구간을 삭제한다.")
    void delete() {
        final Section section2 = new Section(2L,3L,10);
        Long stationId = sectionDao.save(section2);
        sectionDao.deleteById(stationId);
        final int expected = 1;

        final String sql = "SELECT COUNT(*) FROM SECTION";
        final int actual = jdbcTemplate.queryForObject(sql, Integer.class);

        assertThat(actual).isEqualTo(expected);
    }
}
