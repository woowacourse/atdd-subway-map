package wooteco.subway.admin.acceptance;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

public class AcceptanceTest {
	@LocalServerPort
	int port;

	public static RequestSpecification given() {
		return RestAssured.given().log().all();
	}

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	LineResponse getLine(Long id) {
		return given().when().
			get("/api/lines/" + id).
			then().
			log().all().
			extract().as(LineResponse.class);
	}

	void createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("color", "임시컬러");
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

	void updateLine(Long id, String name, LocalTime startTime, LocalTime endTime) {
		Map<String, String> params = new HashMap<>();
		params.put("color", "임시컬러");
		params.put("name", name);
		params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			put("/api/lines/" + id).
			then().
			log().all().
			statusCode(HttpStatus.OK.value());
	}

	List<LineResponse> getLines() {
		return
			given().
				when().
				get("/api/lines").
				then().
				log().all().
				extract().
				jsonPath().getList(".", LineResponse.class);
	}

	void deleteLine(Long id) {
		given().
			when().
			delete("/api/lines/" + id).
			then().
			log().all();
	}

	void createStation(String name) {
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

	List<StationResponse> getStations() {
		return given().
			when().
			get("/api/stations").
			then().
			log().all().
			extract().
			jsonPath().getList(".", StationResponse.class);
	}

	void deleteStation(Long id) {
		given().
			when().
			delete("/api/stations/" + id).
			then().
			log().all();
	}

	void addStationToLine(Long lineId, Long stationId) {
		Map<String, Object> params = new HashMap<>();
		params.put("lineId", lineId);
		params.put("stationId", stationId);
		params.put("duration", 1);
		params.put("distance", 1);

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

	LineResponse findStationsByLineId(Long lineId) {
		return
			given().
				when().
				get(String.format("/api/lines/%d/stations", lineId)).
				then().
				log().all().
				extract().as(LineResponse.class);
	}

	void deleteStation(Long lineId, Long stationId) {
		given().
			when().
			delete(String.format("/api/lines/%d/stations/%d", lineId, stationId)).
			then().
			log().all();
	}
}
