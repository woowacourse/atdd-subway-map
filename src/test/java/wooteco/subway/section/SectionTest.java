package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SectionDao sectionDao;
    private LineDao lineDao;
    private StationDao stationDao;
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        this.sectionDao = new SectionDao(jdbcTemplate);
        this.lineDao = new LineDao(jdbcTemplate);
        this.stationDao = new StationDao(jdbcTemplate);
        this.sectionService = new SectionService(stationDao, lineDao, sectionDao);

        stationDao.save(new Station("테스트 역1"));
        stationDao.save(new Station("테스트 역2"));
        stationDao.save(new Station("테스트 역3"));
        stationDao.save(new Station("테스트 역4"));

        lineDao.save(new Line(1L, "테스트 라인1", "BLACK"));

        sectionDao.save(new Section(1L, 1L, 2L, 10));
        sectionDao.save(new Section(1L, 2L, 3L, 10));
    }

    @DisplayName("상행 종점 등록")
    @Test
    void insertNewUpStation() {
        sectionService.insertSection(1L, new SectionRequest(4L, 1L, 10));

        assertThat(sectionService.findStationsInLine(1L)).hasSize(4);
        assertThat(sectionService.findSectionEndPoint(1L).getUpStationId()).isEqualTo(4L);
    }

    @DisplayName("하행 종점 등록")
    @Test
    void insertNewDownStation() {
        sectionService.insertSection(1L, new SectionRequest(3L, 4L, 10));

        assertThat(sectionService.findStationsInLine(1L)).hasSize(4);
        assertThat(sectionService.findSectionEndPoint(1L).getDownStationId()).isEqualTo(4L);
    }

    @DisplayName("구간 중간에 삽입되며 상행이 이미 존재하는 경우")
    @Test
    void insertMidExistUpStation() {
        sectionService.insertSection(1L, new SectionRequest(1L, 4L, 5));

        assertThat(sectionService.findStationsInLine(1L)).hasSize(4);

        for (Station station : sectionService.findStationsInLine(1L)) {
            System.out.println(station.getName());
        }
    }

    @DisplayName("구간 중간에 삽입되며 상행이 이미 존재하는 경우 거리값 변경 확인")
    @Test
    void insertMidExistUpStationDistance() {
        sectionService.insertSection(1L, new SectionRequest(1L, 4L, 4));

        assertThat(sectionService.findByUpStationId(1L, 1L).getDistance()).isEqualTo(4);
        assertThat(sectionService.findByDownStationId(1L, 4L).getDistance()).isEqualTo(6);
    }


}
