package wooteco.subway.section.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.model.Line;
import wooteco.subway.section.model.Section;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.model.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        sectionDao = new SectionDao(jdbcTemplate);
        lineDao = new LineDao(jdbcTemplate);
        stationDao = new StationDao(jdbcTemplate);
    }

    @DisplayName("노선 ID에 해당하는 구간들 조회")
    @Test
    void findSections() {
        //given
        Long stationId1 = stationDao.save(new Station("강남역"));
        Long stationId2 = stationDao.save(new Station("잠실역"));
        Long lineId = lineDao.save(new Line("2호선", "green"));

        sectionDao.save(Section.builder()
                        .line(lineDao.findLineById(lineId))
                        .upStation(stationDao.findStationById(stationId1))
                        .downStation(stationDao.findStationById(stationId2))
                        .distance(10)
                        .build());
        //when
        List<Section> sections = sectionDao.findSectionsByLineId(lineId);
        //then
        assertThat(sections).hasSize(1);
    }
}