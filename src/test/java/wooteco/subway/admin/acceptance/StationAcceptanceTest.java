package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class StationAcceptanceTest {
	@LocalServerPort
	int port;
	private AcceptanceTest acceptanceTest = new AcceptanceTest();

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@DisplayName("지하철역을 관리한다")
	@Test
	void manageStation() {
		acceptanceTest.createStation("잠실역");
		acceptanceTest.createStation("종합운동장역");
		acceptanceTest.createStation("선릉역");
		acceptanceTest.createStation("강남역");

		List<StationResponse> stations = acceptanceTest.getStations();
		assertThat(stations.size()).isEqualTo(4);

		acceptanceTest.deleteStation(stations.get(0).getId());

		List<StationResponse> stationsAfterDelete = acceptanceTest.getStations();
		assertThat(stationsAfterDelete.size()).isEqualTo(3);
	}
}
