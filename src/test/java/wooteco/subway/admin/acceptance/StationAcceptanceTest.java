package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.admin.dto.StationResponse;

public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 관리한다")
    @Test
    void manageStation() {
        createStation("잠실역");
        createStation("종합운동장역");
        createStation("선릉역");
        createStation("강남역");

        List<StationResponse> stations = getStations();
        assertThat(stations.size()).isEqualTo(4);

        deleteStation(stations.get(0).getId());

        List<StationResponse> stationsAfterDelete = getStations();
        assertThat(stationsAfterDelete.size()).isEqualTo(3);
        assertThat(stationsAfterDelete.stream()
                .map(StationResponse::getName)).doesNotContain("잠실역");
    }

    private void deleteStation(Long id) {
        given().
        when().
                delete("/stations/" + id).
        then().
                log().all();
    }
}
