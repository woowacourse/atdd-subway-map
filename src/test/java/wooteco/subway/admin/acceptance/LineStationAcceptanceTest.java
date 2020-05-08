package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

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

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
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
		// Given 지하철역이 여러 개 추가되어있다.
		// And 지하철 노선이 추가되어있다.
		createLine("신분당선");
		createStation("잠실역");
		createStation("종합운동장역");
		LineResponse line = getLines().get(0);
		StationResponse station = getStations().get(0);

		// When 지하철 노선에 지하철역을 등록하는 요청을 한다. - LINE
		addStationToLine(line.getId(), station.getId());
		// Then 지하철역이 노선에 추가 되었다
		assertThat(findStationsByLineId(line.getId())).isNotNull();

		// When 지하철 노선의 지하철역 목록 조회 요청을 한다. - LINE
		List<StationResponse> stationResponses = findStationsByLineId(line.getId());
		// Then 지하철역 목록을 응답 받는다.
		assertThat(stationResponses.size()).isNotEqualTo(0);
		// And 새로 추가한 지하철역을 목록에서 찾는다.
		assertThat(stationResponses).contains(station);

		// When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다. - LINE
		deleteStation(line.getId(), station.getId());
		// Then 지하철역이 노선에서 제거 되었다.
		stationResponses = findStationsByLineId(line.getId());
		assertThat(stationResponses).doesNotContain(station);

		// When 지하철 노선의 지하철역 목록 조회 요청을 한다.  - LINE
		stationResponses = findStationsByLineId(line.getId());
		// Then 지하철역 목록을 응답 받는다.
		assertThat(stationResponses).isNotNull();
		// And 제외한 지하철역이 목록에 존재하지 않는다.
		assertThat(stationResponses).doesNotContain(station);
	}

	private void addStationToLine(Long lineId, Long stationId) {
		Map<String, Object> params = new HashMap<>();
		params.put("lineId", lineId);
		params.put("stationId", stationId);

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post(String.format("/api/lines/%d/stations/%d", lineId, stationId)).
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private List<StationResponse> findStationsByLineId(Long lineId) {
		return
			given().
				when().
				get(String.format("/api/lines/%d/stations", lineId)).
				then().
				log().all().
				extract().
				jsonPath().getList(".", StationResponse.class);
	}

	private void deleteStation(Long lineId, Long stationId) {
		given().
			when().
			delete(String.format("/api/lines/%d/stations/%d", lineId, stationId)).
			then().
			log().all()
			.statusCode(HttpStatus.OK.value());
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
			post("/stations").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private List<StationResponse> getStations() {
		return given().
			when().
			get("/stations").
			then().
			log().all().
			extract().
			jsonPath().getList(".", StationResponse.class);
	}
}