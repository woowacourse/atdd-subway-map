package wooteco.subway.admin.acceptance;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
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
public class AcceptanceTest {
	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	static RequestSpecification given() {
		return RestAssured.given().log().all();
	}

	LineResponse getLine(Long id) {
		return given().
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			get("/lines/" + id).
			then().
			log().all().
			extract().as(LineResponse.class);
	}

	void createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		params.put("backgroundColor", "bg-yellow-800");

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

	void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
		Map<String, String> params = new HashMap<>();
		params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		params.put("name", "100호선");
		params.put("backgroundColor", "bg-yellow-800");

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			put("/lines/" + id).
			then().
			log().all().
			statusCode(HttpStatus.OK.value());
	}

	List<LineResponse> getLines() {
		return
			given().
				accept(MediaType.APPLICATION_JSON_VALUE).
				when().
				get("/lines").
				then().
				log().all().
				extract().
				jsonPath().getList(".", LineResponse.class);
	}

	void deleteLine(Long id) {
		given().
			when().
			delete("/lines/" + id).
			then().
			log().all().
			statusCode(HttpStatus.NO_CONTENT.value());
	}

	void deleteLineStation(Long lineId, Long stationId) {
		given().
			when().
			delete("/lines/" + lineId + "/stations/" + stationId).
			then().
			log().all().
			statusCode(HttpStatus.NO_CONTENT.value());
	}

	void createStation(String name) {
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

	List<StationResponse> getStations() {
		return given().
			when().
			get("/stations").
			then().
			log().all().
			extract().
			jsonPath().getList(".", StationResponse.class);
	}

	void createLineStation(Long preStationId, Long stationId, Long lineId) {
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

	List<LineStationResponse> getLineStations(Long lineId) {
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

	void deleteStation(Long id) {
		given().
			when().
			delete("/stations/" + id).
			then().
			log().all().
			statusCode(HttpStatus.NO_CONTENT.value());
	}
}
