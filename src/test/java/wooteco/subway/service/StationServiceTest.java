package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;

@SpringBootTest
@Transactional
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        StationRequest stationRequest = new StationRequest("선릉역");

        assertDoesNotThrow(() -> stationService.createStation(stationRequest));
    }

    @DisplayName("중복된 이름으로 지하철역을 생성하려고 하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        StationRequest stationRequest = new StationRequest("선릉역");
        stationService.createStation(stationRequest);

        assertThatThrownBy(() -> stationService.createStation(stationRequest))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("이미 같은 이름의 지하철역이 존재합니다.");
    }

    @DisplayName("지하철역을 모두 조회한다.")
    @Test
    void findAllStation() {
        StationRequest stationRequest1 = new StationRequest("선릉역");
        stationService.createStation(stationRequest1);

        StationRequest stationRequest2 = new StationRequest("강남역");
        stationService.createStation(stationRequest2);

        List<StationResponse> stationResponses = stationService.findAllStations();
        List<String> stationNameResponses = stationResponses.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(stationResponses).hasSize(2),
                () -> assertThat(stationNameResponses).contains("선릉역", "강남역")
        );
    }

    @DisplayName("존재하지 않는 id로 지하철역을 찾으면 예외가 발생한다.")
    @Test
    void findByWrongId() {
        StationRequest stationRequest = new StationRequest("선릉역");
        stationService.createStation(stationRequest);

        assertThatThrownBy(() -> stationService.findById(0L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당 지하철역이 존재하지 않습니다.");
    }

    @DisplayName("존재하지 않는 id로 지하철역을 찾으면 예외가 발생한다.")
    @Test
    void deleteByWrongId() {
        StationRequest stationRequest = new StationRequest("선릉역");
        stationService.createStation(stationRequest);

        assertThatThrownBy(() -> stationService.deleteStation(0L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("해당 지하철역이 존재하지 않습니다.");
    }
}
