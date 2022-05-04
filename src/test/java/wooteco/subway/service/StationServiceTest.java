package wooteco.subway.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dao.FakeStationDao;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.ClientException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationServiceTest {

    private StationService stationService;

    @BeforeEach
    void setUp() {
        stationService = new StationService(new FakeStationDao());
    }

    @AfterEach
    void finish() {
        List<StationResponse> stations = stationService.findAll();
        for (StationResponse station : stations) {
            stationService.deleteStation(station.getId());
        }
    }

    @Test
    @DisplayName("역정보 저장")
    void save() {
        StationRequest station = new StationRequest("역삼역");
        StationResponse newStation = stationService.createStation(station);

        assertThat(station.getName()).isEqualTo(newStation.getName());
    }

    @DisplayName("중복된 역이름 저장시 예외")
    @Test
    void duplicateStation() {
        StationRequest station = new StationRequest("역삼역");
        StationRequest duplicateStation = new StationRequest("역삼역");
        stationService.createStation(station);

        assertThatThrownBy(() -> stationService.createStation(duplicateStation))
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("이미 등록된 지하철역입니다.");
    }

    @Test
    @DisplayName("역정보들 조회")
    void findAll() {
        StationRequest firstStation = new StationRequest("역삼역");
        StationRequest secondStation = new StationRequest("삼성역");
        stationService.createStation(firstStation);
        stationService.createStation(secondStation);

        List<StationResponse> stations = stationService.findAll();
        stations.stream()
                .filter(station -> station.getName().equals("역삼역"))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지하철 정보가 없습니다."));
    }

    @Test
    @DisplayName("역정보를 삭제")
    void deleteStation() {
        StationRequest station = new StationRequest("역삼역");
        StationResponse newStation = stationService.createStation(station);

        assertThat(stationService.deleteStation(newStation.getId())).isEqualTo(1);
    }
}
