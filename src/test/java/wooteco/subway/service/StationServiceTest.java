package wooteco.subway.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationDao stationDao;

    @InjectMocks
    private StationService stationService;

    private final Station romaStation = new Station(1L, "roma");
    private final StationRequest romaRequest = new StationRequest("roma");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void save() {
        // given
        when(stationDao.findByName("roma"))
                .thenReturn(Optional.empty());
        when(stationDao.save(any(Station.class)))
                .thenReturn(romaStation);
        // when
        StationResponse result = stationService.save(romaRequest);
        // then
        assertThat(result.getName()).isEqualTo("roma");
    }

    @DisplayName("지하철 역 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void save_duplication_exception() {
        // given
        when(stationDao.findByName("roma"))
                .thenReturn(Optional.of(romaStation));
        // then
        assertThatThrownBy(() -> stationService.save(romaRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("roma : 중복되는 지하철역이 존재합니다.");
    }

    @DisplayName("존재하는 모든 지하철역을 조회한다.")
    @Test
    void findAll() {
        // given
        when(stationDao.findAll())
                .thenReturn(List.of(romaStation, new Station(2L, "brown")));
        // when
        List<StationResponse> result = stationService.findAll();
        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).getId()).isEqualTo(1L),
                () -> assertThat(result.get(0).getName()).isEqualTo("roma"),
                () -> assertThat(result.get(1).getId()).isEqualTo(2L),
                () -> assertThat(result.get(1).getName()).isEqualTo("brown")
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void delete() {
        // given
        when(stationDao.findById(1L))
                .thenReturn(Optional.of(romaStation));
        // when
        stationService.delete(1L);
        // then
        verify(stationDao).delete(any(Station.class));
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철역이 없다면 에러를 응답한다.")
    @Test
    void delete_noExistStation_exception() {
        // given
        when(stationDao.findById(1L))
                .thenReturn(Optional.empty());
        // then
        assertThatThrownBy(() -> stationService.delete(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("1 : 해당 ID의 지하철역이 존재하지 않습니다.");
    }
}
