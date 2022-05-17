package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Section;

@JdbcTest
@Sql("/schema.sql")
class JdbcSectionDaoTest {

    private final SectionDao jdbcSectionDao;

    @Autowired
    public JdbcSectionDaoTest(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcSectionDao = new SectionDao(jdbcTemplate, dataSource);
    }

    @Test
    @DisplayName("구간을 생성한다.")
    void save() {
        Section newSection = jdbcSectionDao.save(new Section(1L, 1L, 2L, 5));
        assertThat(newSection.getId()).isNotNull();
    }

    @Test
    @DisplayName("노선 아이디로 구간들을 조회한다.")
    void findByLineId() {
        Section section = jdbcSectionDao.save(new Section(1L, 1L, 2L, 5));
        Section section1 = jdbcSectionDao.save(new Section(1L, 2L, 3L, 5));

        List<Section> sections = jdbcSectionDao.findByLineId(1L);
        assertThat(sections).hasSize(2)
                .extracting("id")
                .containsExactly(section.getId(), section1.getId());
    }

    @Test
    @DisplayName("구간을 업데이트 한다.")
    void update() {
        Section section = jdbcSectionDao.save(new Section(1L, 1L, 2L, 5));
        Section updateSection = new Section(1L, 3L, 4L, 2);
        section.updateUpStationId(updateSection);
        section.updateDistance(updateSection);
        jdbcSectionDao.update(List.of(section));

        List<Section> sections = jdbcSectionDao.findByLineId(1L);
        assertThat(sections)
                .extracting("upStationId", "distance")
                .containsExactly(tuple(4L, 3));
    }

    @Test
    @DisplayName("구간을 제거한다.")
    void deleteById() {
        Section section = jdbcSectionDao.save(new Section(1L, 1L, 2L, 5));
        jdbcSectionDao.deleteById(section.getId());

        List<Section> sections = jdbcSectionDao.findByLineId(1L);
        assertThat(sections).isEmpty();
    }
}
