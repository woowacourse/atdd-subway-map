package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dao.dto.SectionDto;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SectionServiceTest {

    private final SectionService sectionService;
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionServiceTest(SectionService sectionService, LineDao lineDao, SectionDao sectionDao,
            StationDao stationDao) {
        this.sectionService = sectionService;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @BeforeEach
    void set() {
        lineDao.save(new Line("2호선", "green"));
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("선릉역"));
        Section section = new Section(1L, stationDao.findById(1L).get(), stationDao.findById(2L).get(), 5);
        sectionDao.save(section);
    }

    @AfterEach
    void reset() {
        lineDao.deleteAll();
        stationDao.deleteAll();
        sectionDao.deleteAll();
    }

    @Test
    @DisplayName("기존의 구간의 길이와 같은 구간을 추가하면 예외를 반환한다")
    void create_inValidDistance_same() {
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("기존의 구간의 길이보다 큰 구간을 추가하면 예외를 반환한다")
    void create_inValidDistance_longer() {
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 8);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("이미 존재하는 구간을 추가하면 예외를 반환한다.")
    void create_inValidStations_bothExist() {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 3);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 구간입니다.");
    }

    @Test
    @DisplayName("상행역과 하행역이 모두 존재하지 않는 구간을 추가하면 예외를 반환한다.")
    void create_inValidStations_bothDoNotExist() {
        stationDao.save(new Station("역삼역"));
        stationDao.save(new Station("잠실역"));

        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 3);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 모두 존재하지 않습니다.");
    }

    @Test
    @DisplayName("갈래길이 생기지 않도록 기존 역을 수정하여 저장한다.")
    void create_updateOrigin() {
        stationDao.save(new Station("잠실역"));

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 3);

        sectionService.create(1L, sectionRequest);
        SectionDto sectionDto = sectionDao.findById(2L).get(0);
        long actualLineId = sectionDto.getLineId();
        long actualUpStationId = sectionDto.getUpStationId();
        long actualDownStationId = sectionDto.getDownStationId();
        int actualDistance = sectionDto.getDistance();

        assertThat(actualLineId).isEqualTo(1L);
        assertThat(actualUpStationId).isEqualTo(1L);
        assertThat(actualDownStationId).isEqualTo(3L);
        assertThat(actualDistance).isEqualTo(3);
    }
}
