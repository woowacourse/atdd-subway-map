package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
public class StationServiceTest {

    @Mock
    private JdbcStationDao jdbcStationDao;

    @InjectMocks
    private StationService stationService;

    @DisplayName("지하철역을 등록한다.")
    @Test
    void createStation() {
        doReturn(1L)
                .when(jdbcStationDao).save("강남역");
        StationResponse stationResponse = stationService.createStation(new StationRequest("강남역"));
        assertAll(
                () -> stationResponse.getId().equals(1L),
                () -> stationResponse.getName().equals("강남역")
        );

    }

    @DisplayName("지하철역 목록들을 조회한다.")
    @Test
    void getStations() {
        doReturn(List.of(new Station("강남역"), new Station("잠실역")))
                .when(jdbcStationDao).findAll();

        List<StationResponse> stationResponses = stationService.getStations();

        assertThat(stationResponses.size()).isEqualTo(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        doReturn(true)
                .when(jdbcStationDao).deleteById(1L);
        boolean isDeleted = stationService.deleteStation(1L);
        assertThat(isDeleted).isTrue();
    }
}
