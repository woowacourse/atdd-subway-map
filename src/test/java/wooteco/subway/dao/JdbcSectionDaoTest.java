package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
@Sql("/sectionDaoTestSchema.sql")
class JdbcSectionDaoTest {

    private final SectionDao sectionDao;

    @Autowired
    public JdbcSectionDaoTest(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        sectionDao = new JdbcSectionDao(jdbcTemplate, dataSource);
    }

    @Test
    @DisplayName("Section을 저장한다.")
    void saveSection() {
        Section section = new Section(
                new Line(1L, "신분당선", "yellow"),
                new Station(1L, "신도림역"), new Station(2L, "왕십리역"),
                6, 1L);

        Long id = sectionDao.save(section);

        assertThat(id).isEqualTo(1L);
    }

    @Test
    @DisplayName("지하철 역이 대상 노선에 존재하는지 확인한다.")
    void existByStationIdAndLineId() {
        // given
        Section givenSection = new Section(
                new Line(1L, "신분당선", "yellow"),
                new Station(1L, "신도림역"), new Station(2L, "왕십리역"),
                6, 1L);
        Long id = sectionDao.save(givenSection);

        // when
        boolean result = sectionDao.existByLineIdAndStationId(1L, 1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("지하철 역이 대상 노선에 존재하지 않는 지 확인한다.")
    void notExistByStationId() {
        // given
        Section givenSection = new Section(
                new Line(1L, "신분당선", "yellow"),
                new Station(1L, "신도림역"), new Station(2L, "왕십리역"),
                6, 1L);
        Long id = sectionDao.save(givenSection);

        // when
        boolean result = sectionDao.existByLineIdAndStationId(1L, 3L);

        // then
        assertThat(result).isFalse();
    }
}
