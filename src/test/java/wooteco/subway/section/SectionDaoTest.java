package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SectionDaoTest {

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

        lineDao.save(new Line(1L, "테스트 라인1", "BLACK"));

        sectionDao.save(new Section(1L, 1L, 2L, 10));
    }

    @DisplayName("구간 추가 성공 테스트")
    @Test
    void successSaveTest() {
        assertDoesNotThrow(() ->
            sectionDao.save(new Section(1L, 1, 2, 10))
        );
    }

    @DisplayName("구간 조회 테스트")
    @Test
    void selectSectionTest() {
        assertDoesNotThrow(() ->
            sectionDao.findByLineId(1L)
        );
    }

    @DisplayName("존재하지 않는 라인의 구간 조회 테스트")
    @Test
    void failSelectSectionTest() {
        assertThatThrownBy(() ->
            sectionDao.findByLineId(2L)
        ).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("구간 조회 크기 테스트")
    @Test
    void findAllStationSize() {
        sectionDao.save(new Section(1L, 2L, 3L, 20));
        List<Section> stations = sectionDao.findStationsByLineId(1L);
        assertThat(stations).hasSize(2);
        System.out.println(stations);
    }

    @DisplayName("종점 찾기 테스트")
    @Test
    void findUpAndDownStation() {
        sectionDao.save(new Section(1L, 2L, 3L, 20));
        RouteInSection sectionEndPoint = sectionService.findSectionEndPoint(1L);

        assertThat(sectionEndPoint).isEqualTo(new RouteInSection(1L, 3L));
    }

    @DisplayName("구간에 속한 모든 역 찾기 테스트")
    @Test
    void findAllStationInSection() {
        sectionDao.save(new Section(1L, 2L, 3L, 20));
        List<Station> stations = sectionService.findStationsInLine(1L);

        assertThat(stations).hasSize(3);

        assertThat(stations.get(0)).isEqualTo(new Station("테스트 역1"));
        assertThat(stations.get(1)).isEqualTo(new Station("테스트 역2"));
        assertThat(stations.get(2)).isEqualTo(new Station("테스트 역3"));
    }

}
