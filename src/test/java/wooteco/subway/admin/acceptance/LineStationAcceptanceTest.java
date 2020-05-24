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
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {
	@LocalServerPort
	int port;
	private AcceptanceTest acceptanceTest = new AcceptanceTest();

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
	@Test
	void manageLineStation() {
		// Given
		acceptanceTest.createLine("신분당선");
		acceptanceTest.createStation("잠실역");
		acceptanceTest.createStation("종합운동장역");
		LineResponse line = acceptanceTest.getLines().get(0); // 신분당
		StationResponse station = acceptanceTest.getStations().get(0); // 잠실

		// When
		acceptanceTest.addStationToLine(line.getId(), station.getId());
		// Then
		LineResponse lineResponse = acceptanceTest.findStationsByLineId(line.getId());
		assertThat(lineResponse.getStations()).isNotNull();

		// When
		List<Station> stations = acceptanceTest.findStationsByLineId(line.getId()).getStations();
		// Then
		assertThat(stations.size()).isNotEqualTo(0);
		assertThat(stations).contains(station.toStation());

		// When
		acceptanceTest.deleteStation(line.getId(), station.getId());
		// Then
		stations = acceptanceTest.findStationsByLineId(line.getId()).getStations();
		assertThat(stations).doesNotContain(station.toStation());

		// When
		stations = acceptanceTest.findStationsByLineId(line.getId()).getStations();
		// Then
		assertThat(stations).isNotNull();
		assertThat(stations).doesNotContain(station.toStation());
	}
}