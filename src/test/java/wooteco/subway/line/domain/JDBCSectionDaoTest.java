package wooteco.subway.line.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.line.domain.*;
import wooteco.subway.station.domain.JDBCStationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class JDBCSectionDaoTest {
    private final JdbcTemplate jdbcTemplate;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    @Autowired
    JDBCSectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionDao = new JDBCSectionDao(jdbcTemplate);
        this.stationDao = new JDBCStationDao(jdbcTemplate);
        this.lineDao = new JDBCLineDao(jdbcTemplate);
    }

    @Test
    void save() {
        //given
        Station station = stationDao.save(new Station("백기역"));
        Station station2 = stationDao.save(new Station("흑기역"));
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));

        //when
        Section section = new Section(line, station, station2, 15);
        Section savedSection = sectionDao.save(section);

        //then
        assertThat(section.line().id()).isEqualTo(savedSection.line().id());
    }
}
