package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Station;

class StationServiceTest {

    private final StationService stationService = new StationService();

    @Test
    @DisplayName("지하철역 추가, 조회, 삭제 테스트")
    void StationCRDTest() {
        stationService.save("station1");
        stationService.save("station2");
        stationService.save("station3");

        List<Station> stations = stationService.findAll();
        
        assertThat(stations).hasSize(3)
                .extracting("name")
                .containsExactly("station1", "station2", "station3");

        stationService.delete(stations.get(0).getId());
        stationService.delete(stations.get(0).getId());
        stationService.delete(stations.get(0).getId());

        assertThat(stations).hasSize(0);
    }
}
