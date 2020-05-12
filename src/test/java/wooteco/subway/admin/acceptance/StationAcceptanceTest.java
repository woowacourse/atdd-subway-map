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
        // Todo: truncate 수행되게 한 뒤 주석 풀것
//        createStation("잠실역");
//        createStation("종합운동장역");
//        createStation("선릉역");
//        createStation("강남역");

        List<StationResponse> stations = getStations();
        stations.forEach(stationResponse -> System.out.println("## " + stationResponse.getName()));
        assertThat(stations.size()).isEqualTo(4);

//        deleteStation(stations.get(0).getName());
//
//        List<StationResponse> stationsAfterDelete = getStations();
//
//        assertThat(stationsAfterDelete.size()).isEqualTo(3);
    }
}
