package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;

import java.util.List;

@DisplayName("지하철역 관련 service 테스트")
@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    private static final Station STATION = new Station("강남역");

    @Mock
    private StationDao stationDao;

    @InjectMocks
    private StationService stationService;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        // when
        stationService.save(STATION);

        // mocking
        given(stationDao.findAll())
                .willReturn(List.of(STATION));

        // then
        assertThat(stationService.findAll()).hasSize(1);
    }

    @DisplayName("중복된 지하철역을 생성할 경우 예외를 발생시킨다.")
    @Test
    void saveDuplicatedName() {
        // given
        stationService.save(STATION);

        // mocking
        given(stationDao.existStationByName(any()))
                .willThrow(new IllegalArgumentException("지하철역 이름이 중복됩니다."));

        // when & then
        assertThatThrownBy(() -> stationService.save(STATION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지하철역 이름이 중복됩니다.");
    }

    @DisplayName("지하철역의 목록을 조회한다.")
    @Test
    void findAll() {
        // given
        stationService.save(STATION);

        // mocking
        given(stationDao.findAll())
                .willReturn(List.of(STATION));

        // when & then
        assertThat(stationService.findAll()).containsExactly(STATION);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void delete() {
        // given
        long stationId = stationService.save(STATION);

        // mocking
        given(stationDao.existStationById(any()))
                .willReturn(true);

        // when & then
        assertThatCode(() -> stationService.delete(stationId))
                .doesNotThrowAnyException();
    }

    @DisplayName("존재하지 않는 지하철역을 삭제할 경우 예외가 발생한다.")
    @Test
    void deleteNotExistStation() {
        // mocking
        given(stationDao.existStationById(any()))
                .willThrow(new IllegalArgumentException("존재하지 않는 지하철역입니다."));

        // when & then
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 지하철역입니다.");
    }
}
