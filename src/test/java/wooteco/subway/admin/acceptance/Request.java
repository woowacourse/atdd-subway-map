package wooteco.subway.admin.acceptance;

import static io.restassured.RestAssured.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.response.Response;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

public class Request {
	public static Long createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("title", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("bgColor", "bg-red-400");
		params.put("intervalTime", "10");

		Response post = given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lines");

		post.then().
			log().all().
			statusCode(HttpStatus.CREATED.value());

		String location = post.header("Location");

		char id = location.charAt(location.length() - 1);
		return Long.valueOf(String.valueOf(id));
	}

	public static void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
		Map<String, String> params = new HashMap<>();
		params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("bgColor", "bg-red-600");
		params.put("intervalTime", "10");

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

	public static List<LineResponse> getLines() {
		return
			given().
				when().
				get("/lines").
				then().
				log().all().
				extract().
				jsonPath().getList(".", LineResponse.class);
	}

	public static void deleteLine(Long id) {
		given().
			when().
			delete("/lines/" + id).
			then().
			log().all();
	}

	public static LineResponse getLine(Long id) {
		return given()
			.when()
			.get("/lines/" + id)
			.then()
			.log().all()
			.extract().as(LineResponse.class);
	}

	public static Long createStation(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		Response post = given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/stations");

		post.then().
			log().all()
			.statusCode(HttpStatus.CREATED.value());

		String location = post.header("Location");

		char id = location.charAt(location.length() - 1);
		return Long.valueOf(String.valueOf(id));
	}

	public static List<StationResponse> getStations(Long id) {
		return given()
			.when()
			.get("/lines/" + id + "/stations")
			.then()
			.log().all()
			.extract()
			.jsonPath().getList(".", StationResponse.class);
	}

	public static void addLineStation(Long lineId, Long preStationId, Long stationId) {
		Map<String, String> params = new HashMap<>();
		params.put("preStationId", String.valueOf(preStationId));
		params.put("stationId", String.valueOf(stationId));
		params.put("distance", "1000");
		params.put("duration", "5");

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			put("/lines/" + lineId + "/stations").
			then().
			log().all().statusCode(HttpStatus.OK.value());
	}

	public static void deleteLineStation(Long lineId, Long stationId) {
		given()
			.when()
			.delete("/lines/" + lineId + "/stations/" + stationId)
			.then()
			.log().all().statusCode(HttpStatus.NO_CONTENT.value());
	}
}
