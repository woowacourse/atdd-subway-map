package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Section;

import java.util.List;

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

    @DisplayName("노선에 해당하는 Section 객체들을 가져오는 것을 확인한다.")
    @Test
    void find_sections_by_line_id() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final List<Section> sections = sectionDao.findSectionsByLineId(1L);
        final int actual = sections.size();

        assertThat(actual).isEqualTo(1);
    }

    @DisplayName("노선에 해당하는 Section 객체들이 없을 때 빈 리스트를 가져오는 것을 확인한다.")
    @Test
    void find_sections_by_line_id_when_empty() {
        final List<Section> sections = sectionDao.findSectionsByLineId(1L);
        final int actual = sections.size();

        assertThat(actual).isEqualTo(0);
    }

    @DisplayName("노선에 해당하는 구간의 개수를 확인한다.")
    @Test
    void counts_by_line() {
        final Section section = new Section(1L, 2L, 10);
        sectionDao.save(1L, section);
        final int actual = sectionDao.countsByLine(1L);
        assertThat(actual).isEqualTo(1);
    }
}