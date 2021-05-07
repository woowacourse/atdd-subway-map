package wooteco.subway.line.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestConstructor;
import wooteco.subway.station.domain.DBStationDao;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationDao;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
class DBSectionDaoTest {

    private final JdbcTemplate jdbcTemplate;
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    @Autowired
    DBSectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionDao = new DBSectionDao(jdbcTemplate);
        this.stationDao = new DBStationDao(jdbcTemplate);
        this.lineDao = new DBLineDao(jdbcTemplate);
    }

    @BeforeEach
    void init() {

    }

    @Test
    void save() {
        //given
        Station station = stationDao.save(new Station("백기역"));
        Station station2 = stationDao.save(new Station("흑기역"));
        Line line = lineDao.save(new Line("신분당선", "bg-red-600"));

        //when
        Section section = new Section(line.id(), station.getId(), station2.getId(), 15);
        Section savedSection = sectionDao.save(section);

        //then
        assertThat(section.lineId()).isEqualTo(savedSection.lineId());
    }

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }

    @Test
    void delete() {
    }
}