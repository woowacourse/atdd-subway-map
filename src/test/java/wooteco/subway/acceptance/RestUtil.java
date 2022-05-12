package wooteco.subway.acceptance;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.StationRequest;
import wooteco.subway.controller.dto.StationResponse;

public class RestUtil {

	public static ExtractableResponse<Response> post(StationRequest stationRequest) {
		return RestAssured.given()
			.body(stationRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then()
			.extract();
	}

	public static ExtractableResponse<Response> post(LineRequest lineRequest) {
		return RestAssured.given()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then()
			.extract();
	}

	public static ExtractableResponse<Response> get(String url) {
		return RestAssured.given()
			.when()
			.get(url)
			.then()
			.extract();
	}


	public static Long getIdFromStation(ExtractableResponse<Response> response) {
		return response.jsonPath()
			.getObject(".", StationResponse.class)
			.getId();
	}

	public static Long getIdFromLine(ExtractableResponse<Response> response) {
		return response.jsonPath()
			.getObject(".", LineResponse.class)
			.getId();
	}

	public static List<Long> getIdsFromStation(ExtractableResponse<Response> response) {
		return response.jsonPath().getList(".", StationResponse.class).stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());
	}

	public static List<Long> getIdsFromLine(ExtractableResponse<Response> response) {
		return response.jsonPath().getList(".", LineResponse.class).stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());
	}

	public static <T> T toResponseDto(ExtractableResponse<Response> response, Class<T> responseClass) {
		return response.body()
			.jsonPath()
			.getObject(".", responseClass);
	}
}
