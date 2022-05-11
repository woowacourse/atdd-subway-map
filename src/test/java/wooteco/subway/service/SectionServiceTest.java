package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.service.dto.SectionRequest;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@DisplayName("구간 관련 service 테스트")
@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    @InjectMocks
    private SectionService sectionService;

    @DisplayName("구간을 생성한다.")
    @Test
    void save() {
        // mocking
        given(stationDao.existStationById(1L))
                .willReturn(true);
        given(stationDao.existStationById(2L))
                .willReturn(true);
        given(sectionDao.existUpStation(1L, 2L))
                .willReturn(true);

        // when & then
        assertThatCode(
                () -> sectionService.save(1L, new SectionRequest(1L, 2L, 10))
        ).doesNotThrowAnyException();
    }

    @DisplayName("구간 생성 시 상행역에 해당하는 지하철역이 존재하지 경우 예외가 발생한다.")
    @Test
    void saveNotExistUpStation() {
        // mocking
        given(stationDao.existStationById(1L))
                .willReturn(false);

        // when & then
        assertThatThrownBy(
                () -> sectionService.save(1L, new SectionRequest(1L, 2L, 10))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역이 존재하지 않습니다.");
    }

    @DisplayName("구간 생성 시 하행역에 해당하는 지하철역이 존재하지 경우 예외가 발생한다.")
    @Test
    void saveNotExistDownStation() {
        // mocking
        given(stationDao.existStationById(1L))
                .willReturn(true);
        given(stationDao.existStationById(2L))
                .willReturn(false);

        // when & then
        assertThatThrownBy(
                () -> sectionService.save(1L, new SectionRequest(1L, 2L, 10))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행역이 존재하지 않습니다.");
    }

    @DisplayName("구간 생성 시 상행역과 하행역이 이미 지하철 노선에 존재하면 예외가 발생한다.")
    @Test
    void saveAlreadyExistAllSection() {
        // mocking
        given(stationDao.existStationById(1L))
                .willReturn(true);
        given(stationDao.existStationById(2L))
                .willReturn(true);
        given(sectionDao.existStation(eq(1L), any()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(
                () -> sectionService.save(1L, new SectionRequest(1L, 2L, 10))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 위치에 구간을 저장할 수 없습니다.");
    }

    @DisplayName("구간 생성 시 상행 종점을 등록한다.")
    @Test
    void saveNewUpStation() {
        // mocking
        given(stationDao.existStationById(1L))
                .willReturn(true);
        given(stationDao.existStationById(2L))
                .willReturn(true);
        given(sectionDao.existUpStation(1L, 2L))
                .willReturn(true);

        // when & then
        assertThatCode(
                () -> sectionService.save(1L, new SectionRequest(1L, 2L, 10))
        ).doesNotThrowAnyException();
    }

    @DisplayName("구간 생성 시 하행 종점을 등록한다.")
    @Test
    void saveNewDownStation() {
        // mocking
        given(stationDao.existStationById(1L))
                .willReturn(true);
        given(stationDao.existStationById(2L))
                .willReturn(true);
        given(sectionDao.existUpStation(1L, 2L))
                .willReturn(false);
        given(sectionDao.existDownStation(1L, 1L))
                .willReturn(true);

        // when & then
        assertThatCode(
                () -> sectionService.save(1L, new SectionRequest(1L, 2L, 10))
        ).doesNotThrowAnyException();
    }

    @DisplayName("구간 생성 시 다른 구간과 연결되어 있지 않으면 예외가 발생한다.")
    @Test
    void saveNotConnectingStation() {
        // mocking
        given(stationDao.existStationById(999L))
                .willReturn(true);
        given(stationDao.existStationById(888L))
                .willReturn(true);
        given(sectionDao.existUpStation(1L, 888L))
                .willReturn(false);

        // when & then
        assertThatThrownBy(
                () -> sectionService.save(1L, new SectionRequest(999L, 888L, 10))
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 위치에 구간을 저장할 수 없습니다.");
    }
}
