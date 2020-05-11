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
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationResponse;
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
		//given
		createStation("강변");
		createStation("잠실나루");
		createStation("잠실");

		createLine("2호선");
		List<StationResponse> stations = getStations();
		List<LineResponse> lines = getLines();
		LineResponse line = getLine(lines.get(0).getId());

		//when
		createLineStation(null, stations.get(1).getId(), line.getId());
		createLineStation(stations.get(1).getId(), stations.get(2).getId(), line.getId());
		createLineStation(stations.get(2).getId(), stations.get(0).getId(), line.getId());
		//then
		List<LineStationResponse> lineStations = getLineStations(line.getId());
		assertThat(lineStations.size()).isEqualTo(3);

		assertThat(getLine(lines.get(0).getId()).getStations().stream().map(StationResponse::getName))
			.containsExactly("잠실나루", "잠실", "강변");

		//when
		LineStationResponse lineStationResponse = lineStations.get(0);
		//then
		assertThat(lineStationResponse.getLineId()).isEqualTo(line.getId());
		assertThat(lineStationResponse.getStationId()).isEqualTo(stations.get(1).getId());

		//given
		createStation("테스트역");

		//when //then
		assertThatThrownBy(() -> createLineStation(null, stations.get(0).getId(), line.getId()));
		//when //then
		assertThatThrownBy(() -> createLineStation(null, stations.get(3).getId(), null));
		//when //then
		assertThatThrownBy(() -> createLineStation(null, Long.MAX_VALUE, line.getId()));
		//when //then
		assertThatThrownBy(() -> createLineStation(Long.MAX_VALUE, stations.get(3).getId(), line.getId()));

		//when
		deleteLineStation(line.getId(), stations.get(1).getId());
		//then
		List<LineStationResponse> lineStationsAfterDelete = getLineStations(line.getId());
		assertThat(lineStationsAfterDelete.size()).isEqualTo(2);
		//and
		boolean isExistLineStation = isExistLineStation(lineStationResponse, lineStationsAfterDelete);
		assertThat(isExistLineStation).isFalse();

		//when //then
		assertThatThrownBy(() -> deleteLineStation(Long.MAX_VALUE, stations.get(0).getId()));

		//when //then
		assertThatThrownBy(() -> deleteLineStation(line.getId(), Long.MAX_VALUE));
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
			log().all().
			statusCode(HttpStatus.NO_CONTENT.value());
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

	private void createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		params.put("bgColor", "bg-yellow-800");

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lines").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private List<LineResponse> getLines() {
		return given().
			when().
			get("/lines").
			then().
			log().all().
			extract().
			jsonPath().getList(".", LineResponse.class);
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
		return given().
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			get("/lines/" + id).
			then().
			log().all().
			extract().as(LineResponse.class);
	}

	private void createLineStation(Long preStationId, Long stationId, Long lineId) {
		int distance = 10;
		int duration = 2;
		LineStationCreateRequest lineStationRequest = new LineStationCreateRequest(preStationId, stationId, distance,
			duration);

		given().
			body(lineStationRequest).
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
				accept(MediaType.APPLICATION_JSON_VALUE).
				when().
				get("/lines/" + lineId + "/stations").
				then().
				log().all().
				extract().
				jsonPath().getList(".", LineStationResponse.class);
	}
}
