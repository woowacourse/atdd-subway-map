package wooteco.subway.admin.acceptance;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.service.dto.StationResponse;

public abstract class AcceptanceTest {

	protected static RequestSpecification given() {
		return RestAssured.given()
			.log().all();
	}

	protected LineResponse getLine(Long id) {
		return given()
			.when()
			.get("/lines/" + id)
			.then()
			.log().all()
			.extract().as(LineResponse.class);
	}

	protected Long createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("bgColor", "bg-red-400");
		params.put("intervalTime", "10");

		return given()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.extract().as(LineResponse.class)
			.getId();
	}

	protected void createDuplicatedLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("bgColor", "bg-red-400");
		params.put("intervalTime", "10");

		given()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then()
			.log()
			.all()
			.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	protected void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
		Map<String, String> params = new HashMap<>();
		params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("bgColor", "bg-red-600");
		params.put("intervalTime", "10");

		given()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + id)
			.then()
			.log()
			.all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	protected void deleteLine(Long id) {
		given()
			.when()
			.delete("/lines/" + id)
			.then()
			.log().all();
	}

	protected List<LineResponse> getLines() {
		return given()
			.when()
			.get("/lines")
			.then()
			.log().all()
			.extract()
			.jsonPath().getList(".", LineResponse.class);
	}

	protected Long createStation(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		return given()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value())
			.extract().as(StationResponse.class).getId();
	}

	protected List<StationResponse> getStation(Long id) {
		return given()
			.when()
			.get("/lines/" + id + "/stations")
			.then()
			.log().all()
			.extract()
			.jsonPath().getList(".", StationResponse.class);
	}

	protected void deleteStation(Long id) {
		given()
			.when()
			.delete("/stations/" + id)
			.then()
			.log().all();
	}

	protected List<StationResponse> getStations() {
		return given()
			.when()
			.get("/stations")
			.then()
			.log().all()
			.extract()
			.jsonPath().getList(".", StationResponse.class);
	}

	protected void addLineStation(Long lineId, Long preStationId, Long stationId) {
		Map<String, String> params = new HashMap<>();
		params.put("preStationId", String.valueOf(preStationId));
		params.put("stationId", String.valueOf(stationId));
		params.put("distance", "1000");
		params.put("duration", "5");

		given()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + lineId + "/stations")
			.then()
			.log().all().statusCode(HttpStatus.NO_CONTENT.value());
	}

	protected void deleteLineStation(Long lineId, Long stationId) {
		given()
			.when()
			.delete("/lines/" + lineId + "/stations/" + stationId)
			.then()
			.log().all();
	}

}
