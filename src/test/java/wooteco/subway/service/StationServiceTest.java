package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.station.DuplicatedStationNameException;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao());
    }


    @DisplayName("추가하려는 역의 이름이 이미 존재하면 예외를 발생시킨다.")
    @Test
    void createStation_exception() {
        StationRequest stationRequest = new StationRequest("서울역");
        stationService.save(stationRequest);

        assertThatThrownBy(() -> stationService.save(stationRequest))
                .isInstanceOf(DuplicatedStationNameException.class);
    }

    @DisplayName("새로운 역을 추가할 수 있다.")
    @Test
    void createStation_success() {
        StationResponse stationResponse = stationService.save(new StationRequest("서울역"));

        assertThat(stationResponse.getName()).isEqualTo("서울역");
    }

    @Test
    @DisplayName("Id값에 해당 하는 지하철 역을 삭제한다.")
    void deleteById() {
        stationService.save(new StationRequest("서울역"));
        stationService.deleteById(1L);

        assertThat(stationService.findAll()).isEmpty();
    }

    @DisplayName("모든 역을 조회한다.")
    @Test
    void findAll() {
        StationRequest stationRequest1 = new StationRequest("서울역");
        StationRequest stationRequest2 = new StationRequest("선릉역");
        stationService.save(stationRequest1);
        stationService.save(stationRequest2);

        List<StationResponse> stationResponses = stationService.findAll();

        assertAll(
                () -> assertThat(stationResponses.get(0).getName()).isEqualTo("서울역"),
                () -> assertThat(stationResponses.get(1).getName()).isEqualTo("선릉역"),
                () -> assertThat(stationResponses.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stationResponses.get(1).getId()).isEqualTo(2L)
        );
    }
}
