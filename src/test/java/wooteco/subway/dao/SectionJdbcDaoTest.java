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
        Station 강남역 = stationJdbcDao.save(new StationRequest("강남역"));
        Station 서초역 = stationJdbcDao.save(new StationRequest("서초역"));

        LineRequest line = new LineRequest("분당선", "bg-red-600",
                강남역.getId(), 서초역.getId(), 10);
        Line lineResponse = lineJdbcDao.save(line);

        Section section = sectionJdbcDao.save(lineResponse.getId(), new Section(0L, 1L, 강남역.getId(), 서초역.getId(), 10));

        assertThat(section.getUpStationId()).isEqualTo(강남역.getId());
        assertThat(section.getDownStationId()).isEqualTo(서초역.getId());
    }

    @DisplayName("구간 정보 삭제")
    @Test
    void delete() {
        stationJdbcDao.save(new StationRequest("강남역"));
        stationJdbcDao.save(new StationRequest("서초역"));
        stationJdbcDao.save(new StationRequest("잠실역"));

        LineRequest line = new LineRequest("분당선", "bg-red-600",
                1L, 2L, 10);
        lineJdbcDao.save(line);

        sectionJdbcDao.save(1L, new Section(0L, 1L, 1L, 2L, 10));
        sectionJdbcDao.save(1L, new Section(0L, 1L, 2L, 3L, 10));

        sectionJdbcDao.delete(1L, new Section(1L, 1L, 1L, 2L, 10));

        assertThat(sectionJdbcDao.find(1L).getSections().size()).isOne();
        lineJdbcDao.delete(1L);
    }
}