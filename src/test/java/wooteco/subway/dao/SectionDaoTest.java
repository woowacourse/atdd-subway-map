package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.entity.LineEntity;
import wooteco.subway.domain.entity.SectionEntity;

@JdbcTest
class SectionDaoTest {

    private SectionDao sectionDao;
    private StationDao stationDao;
    private LineDao lineDao;

    private Station station1;
    private Station station2;

    private Section testSection1;

    @Autowired
    private SectionDaoTest(JdbcTemplate jdbcTemplate) {
        this.sectionDao = new SectionDao(jdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
        this.stationDao = new StationDao(jdbcTemplate);
    }

    @BeforeEach
    public void setUp() {
        station1 = stationDao.save(new Station("강남역"));
        station2 = stationDao.save(new Station("역삼역"));
        Line testLine1 = new Line(1L, "testName", "black", station1, station2, 10L);
        LineEntity line = lineDao.save(testLine1);
    }

    @Test
    void save() {
//        Section testSection1 = new Section(100L, 1L, station1, station2, 10L);
//        SectionEntity section = sectionDao.save(testSection1);
    }

    @Test
    void findAllByLineId() {
    }
}
