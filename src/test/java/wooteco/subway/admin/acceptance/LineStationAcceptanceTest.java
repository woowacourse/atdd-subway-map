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
import wooteco.subway.admin.dto.LineStationResponse;
import wooteco.subway.admin.dto.Request;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {
	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	public static RequestSpecification given() {
		return RestAssured.given().log().all();
	}

	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
	@Test
	void manageLineStation() {
		//given
		createStation("강변");
		createStation("잠실나루");
		createStation("잠실");
		createLine("2호선");
		List<StationResponse> stations = getStations();
		LineResponse line = getLine(stations.get(0).getId());

		//when
		createLineStation(null, stations.get(0).getId(), line.getId());
		createLineStation(stations.get(0).getId(), stations.get(1).getId(), line.getId());
		createLineStation(stations.get(1).getId(), stations.get(2).getId(), line.getId());
		//then
		List<LineStationResponse> lineStations = getLineStations(line.getId());
		assertThat(lineStations.size()).isEqualTo(3);

		//when
		LineStationResponse lineStationResponse = lineStations.get(0);
		//then
		assertThat(lineStationResponse.getLineId()).isEqualTo(line.getId());
		assertThat(lineStationResponse.getStationId()).isEqualTo(stations.get(0).getId());

		//when
		deleteLineStation(line.getId(), stations.get(0).getId());
		//then
		List<LineStationResponse> lineStationsAfterDelete = getLineStations(line.getId());
		assertThat(lineStationsAfterDelete.size()).isEqualTo(2);
		//and
		boolean isExistLineStation = isExistLineStation(lineStationResponse, lineStationsAfterDelete);
		assertThat(isExistLineStation).isFalse();
	}

	private boolean isExistLineStation(LineStationResponse lineStationResponse,
		List<LineStationResponse> lineStationsAfterDelete) {
		return lineStationsAfterDelete.stream()
			.filter(response -> response.getLineId().equals(lineStationResponse.getLineId()))
			.anyMatch(response -> response.getStationId().equals(lineStationResponse.getStationId()));
	}

	private void deleteLineStation(Long lineId, Long stationId) {
		given().
			when().
			delete("/lines/" + lineId + "/stations/" + stationId).
			then().
			log().all();
	}

	private void createStation(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		given().
			body(new Request<>(params)).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/stations").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private void createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		Request<Map<String, String>> param = new Request<>(params);

		given().
			body(param).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lines").
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

	private LineResponse getLine(Long id) {
		return given().when().
			get("/lines/" + id).
			then().
			log().all().
			extract().as(LineResponse.class);
	}

	private void createLineStation(Long preStationId, Long stationId, Long lineId) {
		Map<String, String> lineStation = new HashMap<>();
		lineStation.put("lineId", Long.toString(lineId));
		if (preStationId == null) {
			lineStation.put("preStationId", null);
		} else {
			lineStation.put("preStationId", Long.toString(preStationId));
		}
		lineStation.put("stationId", Long.toString(stationId));
		lineStation.put("distance", "10");
		lineStation.put("duration", "2");
		Request<Map<String, String>> param = new Request<>(lineStation);

		given().
			body(param).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lines/" + lineId + "/stations").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private List<LineStationResponse> getLineStations(Long lineId) {
		return
			given().
				when().
				get("/lines/" + lineId + "/stations").
				then().
				log().all().
				extract().
				jsonPath().getList(".", LineStationResponse.class);
	}
}
