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
import wooteco.subway.admin.station.service.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class StationAcceptanceTest extends AcceptanceTest {

	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@DisplayName("지하철역을 관리한다")
	@Test
	void manageStation() {
		// When 지하철 역을 추가한다.
		Long jamsilId = createStation("잠실역");
		Long jonghapStadiumId = createStation("종합운동장역");
		Long sunlengId = createStation("선릉역");
		Long gangnamId = createStation("강남역");
		// Then 지하철 역이 추가되었고 총 4개의 역이 존재한다.
		List<StationResponse> stations = getStations();
		assertThat(stations.size()).isEqualTo(4);

		// When 잠실역을 삭제한다.
		deleteStation(jamsilId);
		// Then 잠실 역이 삭제되고 총 3개의 역이 존재한다.
		List<StationResponse> stationsAfterDelete = getStations();
		assertThat(stationsAfterDelete.size()).isEqualTo(3);
	}

}
