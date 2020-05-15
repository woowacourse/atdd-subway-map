package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

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

		assertThatThrownBy(() -> createStation("선릉역"));

		deleteStation(stations.get(0).getId());

		List<StationResponse> stationsAfterDelete = getStations();
		assertThat(stationsAfterDelete.size()).isEqualTo(3);
		assertThat(stationsAfterDelete.stream().map(StationResponse::getName).collect(Collectors.toList()))
			.containsExactly("종합운동장역", "선릉역", "강남역");

		assertThatThrownBy(() -> deleteStation(Long.MAX_VALUE));
	}
}
