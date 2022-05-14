package wooteco.subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionJdbcDaoTest {

    private SectionJdbcDao sectionJdbcDao;
    private LineJdbcDao lineJdbcDao;
    private StationJdbcDao stationJdbcDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        sectionJdbcDao = new SectionJdbcDao(jdbcTemplate);
        lineJdbcDao = new LineJdbcDao(jdbcTemplate);
        stationJdbcDao = new StationJdbcDao(jdbcTemplate);
    }

    @DisplayName("구간 정보 저장")
    @Test
    void save() {
        Station station = stationJdbcDao.save(new Station("강남역"));
        Station secondStation = stationJdbcDao.save(new Station("서초역"));

        Line lineResponse = lineJdbcDao.save(new Line("분당선", "bg-red-600"));
        Section section = sectionJdbcDao.save(lineResponse.getId(), new Section(0L, 1L, station.getId(), secondStation.getId(), 10));

        assertThat(section.getUpStationId()).isEqualTo(station.getId());
        assertThat(section.getDownStationId()).isEqualTo(secondStation.getId());
    }

    @DisplayName("구간 정보 삭제")
    @Test
    void delete() {
        Station gangnam = stationJdbcDao.save(new Station("강남역"));
        Station seocho = stationJdbcDao.save(new Station("서초역"));
        Station jamsil = stationJdbcDao.save(new Station("잠실역"));

        Line line = lineJdbcDao.save(new Line("분당선", "bg-red-600"));
        sectionJdbcDao.save(line.getId(), new Section(line.getId(), gangnam.getId(), seocho.getId(), 10));
        sectionJdbcDao.save(line.getId(), new Section(line.getId(), seocho.getId(), jamsil.getId(), 10));

        sectionJdbcDao.delete(line.getId(), new Section(line.getId(), gangnam.getId(), seocho.getId(), 10));

        assertThat(sectionJdbcDao.findById(line.getId()).getSections().size()).isOne();
    }
}