package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionDaoTest {
    private SectionDao sectionDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void init() {
        sectionDao = new SectionDao(jdbcTemplate);
        jdbcTemplate.execute("DROP TABLE Section IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE Section(" +
                "id bigint auto_increment not null,\n" +
                "line_id bigint not null,\n" +
                "up_station_id bigint not null,\n" +
                "down_station_id bigint not null,\n" +
                "distance int,\n" +
                "primary key(id))");
    }

    @DisplayName("Section 객체의 정보가 제대로 저장되는 것을 확인한다.")
    @Test
    void save() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Section", Integer.class);

        assertThat(actual).isEqualTo(1);
    }
}