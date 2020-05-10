package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {

	@LocalServerPort
	int port;

	public static RequestSpecification given() {
		return RestAssured.given().log().all();
	}

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
	@Test
	void manageLineStation() {
		// Given
		createLine("신분당선");
		createStation("잠실역");
		createStation("종합운동장역");
		LineResponse line = getLines().get(0); // 신분당
		StationResponse station = getStations().get(0); // 잠실

		// When
		addStationToLine(line.getId(), station.getId());
		// Then
		LineResponse lineResponse = findStationsByLineId(line.getId());
		assertThat(lineResponse.getStations()).isNotNull();

		// When
		List<StationResponse> stations = findStationsByLineId(line.getId()).getStations();
		// Then
		assertThat(stations.size()).isNotEqualTo(0);
		assertThat(stations).contains(station);

		// When
		deleteStation(line.getId(), station.getId());
		// Then
		stations = findStationsByLineId(line.getId()).getStations();
		assertThat(stations).doesNotContain(station);

		// When
		stations = findStationsByLineId(line.getId()).getStations();
		// Then
		assertThat(stations).isNotNull();
		assertThat(stations).doesNotContain(station);
	}

	private void addStationToLine(Long lineId, Long stationId) {
		Map<String, Object> params = new HashMap<>();
		params.put("lineId", lineId);
		params.put("stationId", stationId);
		params.put("distance", 10);
		params.put("duration", 10);

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post(String.format("/api/lines/%d/stations", lineId)).
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private LineResponse findStationsByLineId(Long lineId) {
		return
			given().
				when().
				get(String.format("/api/lines/%d/stations", lineId)).
				then().
				log().all().
				extract().as(LineResponse.class);
	}

	private void deleteStation(Long lineId, Long stationId) {
		given().
			when().
			delete(String.format("/api/lines/%d/stations/%d", lineId, stationId)).
			then().
			log().all();
	}

	private List<LineResponse> getLines() {
		return
			given().
				when().
				get("/api/lines").
				then().
				log().all().
				extract().
				jsonPath().getList(".", LineResponse.class);
	}

	private void createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", "무지개색");
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/api/lines").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private void createStation(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/api/stations").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private List<StationResponse> getStations() {
		return given().
			when().
			get("/api/stations").
			then().
			log().all().
			extract().
			jsonPath().getList(".", StationResponse.class);
	}
}