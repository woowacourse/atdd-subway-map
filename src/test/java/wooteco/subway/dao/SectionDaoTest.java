package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

@JdbcTest
@Transactional
class SectionDaoTest {

    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    @Autowired
    private SectionDaoTest(final NamedParameterJdbcTemplate namedParameterJdbcTemplate, final JdbcTemplate jdbcTemplate) {
        this.stationDao = new StationDao(jdbcTemplate);
        this.sectionDao = new SectionDao(namedParameterJdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
    }

    @DisplayName("구간을 저장한다.")
    @Test
    void save() {
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "군자역");
        final Line savedLine = lineDao.save(new Line("5호선", "bg-purple-600"));
        final Section section = new Section(station1, station2, 10, savedLine.getId());
        final Section savedSection = sectionDao.save(section);

        assertAll(
                () -> assertThat(savedSection.getId()).isNotNull(),
                () -> assertThat(savedSection.getUpStation()).isEqualTo(section.getUpStation()),
                () -> assertThat(savedSection.getDownStation()).isEqualTo(section.getDownStation())
        );
    }

    @DisplayName("모든 구간을 불러온다.")
    @Test
    void findAll() {
        final Station station1 = new Station(1L, "아차산역");
        final Station station2 = new Station(2L, "군자역");
        final Station station3 = new Station(3L, "광나루역");
        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        final Line savedLine = lineDao.save(new Line("5호선", "bg-purple-600"));
        final Section section1 = new Section(station1, station2, 10, savedLine.getId());
        final Section section2 = new Section(station2, station3, 10, savedLine.getId());
        sectionDao.save(section1);
        sectionDao.save(section2);

        assertThat(sectionDao.findAll().size()).isEqualTo(2);
    }
}
