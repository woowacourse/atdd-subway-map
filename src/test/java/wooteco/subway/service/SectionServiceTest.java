package wooteco.subway.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.dto.SectionDto;
import wooteco.subway.dao.StationDao;
import wooteco.subway.dto.SectionRequest;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SectionServiceTest {

    private final SectionService sectionService;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionServiceTest(SectionService sectionService, SectionDao sectionDao,
            StationDao stationDao) {
        this.sectionService = sectionService;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @AfterEach
    void reset() {
        stationDao.deleteAll();
        sectionDao.deleteAll();
    }

    @Test
    @DisplayName("기존의 구간의 길이와 같은 구간을 추가하면 예외를 반환한다")
    void create_inValidDistance_same() {
        stationDao.save("강남역");
        stationDao.save("선릉역");
        sectionDao.save(1L, 1L, 2L, 5);

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 5);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("기존의 구간의 길이보다 큰 구간을 추가하면 예외를 반환한다")
    void create_inValidDistance_longer() {
        stationDao.save("강남역");
        stationDao.save("선릉역");
        sectionDao.save(1L, 1L, 2L, 5);

        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 8);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간의 거리보다 크거나 같은 구간은 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("이미 존재하는 구간을 추가하면 예외를 반환한다.")
    void create_inValidStations_bothExist() {
        stationDao.save("강남역");
        stationDao.save("선릉역");
        sectionDao.save(1L, 1L, 2L, 5);

        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 3);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 구간입니다.");
    }

    @Test
    @DisplayName("상행역과 하행역이 모두 존재하지 않는 구간을 추가하면 예외를 반환한다.")
    void create_inValidStations_bothDoNotExist() {
        stationDao.save("강남역");
        stationDao.save("역삼역");
        stationDao.save("선릉역");
        stationDao.save("잠실역");
        sectionDao.save(1L, 1L, 2L, 5);
        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 3);

        assertThatThrownBy(() -> sectionService.create(1L, sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 모두 존재하지 않습니다.");
    }

    @Test
    @DisplayName("갈래길이 생기지 않도록 기존 역을 수정하여 저장한다.")
    void create_updateOrigin() {
        stationDao.save("강남역");
        stationDao.save("선릉역");
        stationDao.save("잠실역");

        long expectedLineId = 1L;
        long expectedUpStationId = 1L;
        long expectedDownStationId = 3L;
        int expectedDistance = 3;

        sectionDao.save(1L, 1L, 2L, 5);
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 3);

        sectionService.create(1L, sectionRequest);
        SectionDto sectionDto = sectionDao.findById(2L).get(0);
        long actualLineId = sectionDto.getLineId();
        long actualUpStationId = sectionDto.getUpStationId();
        long actualDownStationId = sectionDto.getDownStationId();
        int actualDistance = sectionDto.getDistance();

        assertThat(actualLineId).isEqualTo(expectedLineId);
        assertThat(actualUpStationId).isEqualTo(expectedUpStationId);
        assertThat(actualDownStationId).isEqualTo(expectedDownStationId);
        assertThat(actualDistance).isEqualTo(expectedDistance);
    }
}
