package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import wooteco.subway.exception.DeleteMinimumSizeException;
import wooteco.subway.exception.NoSuchDataException;
import wooteco.subway.exception.SectionInsertExistStationsException;
import wooteco.subway.exception.ShortDistanceException;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineEndPoint;
import wooteco.subway.line.SectionRequest;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@JdbcTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class SectionServiceTest {

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
        this.sectionService = new SectionService(stationDao, sectionDao);

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

        assertThat(sectionService.findStationsByLineId(1L)).hasSize(4);
        assertThat(sectionService.findSectionEndPoint(1L).getUpStationId()).isEqualTo(4L);
    }

    @DisplayName("하행 종점 등록")
    @Test
    void insertNewDownStation() {
        sectionService.insertSection(1L, new SectionRequest(3L, 4L, 10));

        assertThat(sectionService.findStationsByLineId(1L)).hasSize(4);
        assertThat(sectionService.findSectionEndPoint(1L).getDownStationId()).isEqualTo(4L);
    }

    @DisplayName("구간 중간에 삽입되며 상행이 이미 존재하는 경우")
    @Test
    void insertMidExistUpStation() {
        sectionService.insertSection(1L, new SectionRequest(2L, 4L, 5));

        assertThat(sectionService.findStationsByLineId(1L)).hasSize(4);

        List<Long> stationId = new ArrayList<>();
        for (Station station : sectionService.findStationsByLineId(1L)) {
            stationId.add(station.getId());
        }
        System.out.println(stationId.toString());
        assertThat(stationId).isEqualTo(Arrays.asList(1L, 2L, 4L, 3L));
    }

    @DisplayName("구간 중간에 삽입되며 상행이 이미 존재하는 경우 거리값 변경 확인")
    @Test
    void insertMidExistUpStationDistance() {
        sectionService.insertSection(1L, new SectionRequest(1L, 4L, 4));

        assertThat(sectionService.findByUpStationId(1L, 1L).getDistance()).isEqualTo(4);
        assertThat(sectionService.findByDownStationId(1L, 4L).getDistance()).isEqualTo(6);
    }

    @DisplayName("구간 중간에 삽입되며 하행이 이미 존재하는 경우")
    @Test
    void insertMidExistDownStation() {
        sectionService.insertSection(1L, new SectionRequest(4L, 2L, 5));

        assertThat(sectionService.findStationsByLineId(1L)).hasSize(4);

        List<Long> stationIds = new ArrayList<>();
        for (Station station : sectionService.findStationsByLineId(1L)) {
            stationIds.add(station.getId());
        }
        assertThat(stationIds).isEqualTo(Arrays.asList(1L, 4L, 2L, 3L));
    }

    @DisplayName("구간 중간에 삽입되며 하행이 이미 존재하는 경우 거리값 변경 확인")
    @Test
    void insertMidExistDownStationDistance() {
        sectionService.insertSection(1L, new SectionRequest(4L, 2L, 4));

        assertThat(sectionService.findByUpStationId(1L, 1L).getDistance()).isEqualTo(6);
        assertThat(sectionService.findByDownStationId(1L, 4L).getDistance()).isEqualTo(4);
    }

    @DisplayName("구간 중간 삽입 시 거리가 충분하지 않은 경우")
    @Test
    void failInsertDistance() {
        assertThatThrownBy(() ->
            sectionService.insertSection(1L, new SectionRequest(4L, 2L, 10))
        ).isInstanceOf(ShortDistanceException.class);
    }

    @DisplayName("노선이 존재하지 않을 경우")
    @Test
    void noExistStation() {
        assertThatThrownBy(() ->
            sectionService.insertSection(1L, new SectionRequest(4L, 4L, 10))
        ).isInstanceOf(NoSuchDataException.class);
    }

    @DisplayName("두 역 모두 해당 노선에 존재하는 경우")
    @Test
    void towStationIsExistLine() {
        assertThatThrownBy(() ->
            sectionService.insertSection(1L, new SectionRequest(1L, 3L, 5))
        ).isInstanceOf(SectionInsertExistStationsException.class);
    }

    @DisplayName("노선의 상행을 지우는 경우")
    @Test
    void deleteFirstStation() {
        sectionService.deleteByUpStationId(1L, 1L);

        List<Long> stationIds = new ArrayList<>();
        for (Station station : sectionService.findStationsByLineId(1L)) {
            stationIds.add(station.getId());
        }

        assertThat(sectionService.findStationsByLineId(1L)).hasSize(2);
        assertThat(stationIds).isEqualTo(Arrays.asList(2L, 3L));
    }

    @DisplayName("노선의 하행을 지우는 경우")
    @Test
    void deleteLastStation() {
        sectionService.deleteByUpStationId(1L, 3L);

        List<Long> stationIds = new ArrayList<>();
        for (Station station : sectionService.findStationsByLineId(1L)) {
            stationIds.add(station.getId());
        }

        assertThat(sectionService.findStationsByLineId(1L)).hasSize(2);
        assertThat(stationIds).isEqualTo(Arrays.asList(1L, 2L));
    }

    @DisplayName("노선의 중간을 지우는 경우")
    @Test
    void deleteMiddleStation() {
        sectionService.deleteByUpStationId(1L, 2L);

        List<Long> stationIds = new ArrayList<>();
        for (Station station : sectionService.findStationsByLineId(1L)) {
            stationIds.add(station.getId());
        }

        assertThat(sectionService.findStationsByLineId(1L)).hasSize(2);
        assertThat(stationIds).isEqualTo(Arrays.asList(1L, 3L));
    }

    @DisplayName("지우려는 역이 없는 경우")
    @Test
    void deleteValidStation() {
        assertThatThrownBy(() ->
            sectionService.deleteByUpStationId(1L, 4L)
        ).isInstanceOf(NoSuchDataException.class);
    }

    @DisplayName("노선이 두 개일 경우는 삭제에 실패한다")
    @Test
    void deleteRemainTwoStation() {
        sectionService.deleteByUpStationId(1L, 2L);
        assertThatThrownBy(() ->
            sectionService.deleteByUpStationId(1L, 1L)
        ).isInstanceOf(DeleteMinimumSizeException.class);
    }

    @DisplayName("종점 찾기 테스트")
    @Test
    void findUpAndDownStation() {
        sectionDao.save(new Section(1L, 2L, 3L, 20));
        LineEndPoint sectionEndPoint = sectionService.findSectionEndPoint(1L);

        assertThat(sectionEndPoint).isEqualTo(new LineEndPoint(2L, 0L));
    }

    @DisplayName("구간에 속한 모든 역 찾기 테스트")
    @Test
    void findAllStationInSection() {
        List<Station> stations = sectionService.findStationsByLineId(1L);

        assertThat(stations).hasSize(3);

        assertThat(stations.get(0)).isEqualTo(new Station("테스트 역1"));
        assertThat(stations.get(1)).isEqualTo(new Station("테스트 역2"));
        assertThat(stations.get(2)).isEqualTo(new Station("테스트 역3"));
    }
}
