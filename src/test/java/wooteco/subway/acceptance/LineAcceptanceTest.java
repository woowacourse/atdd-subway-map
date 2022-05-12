package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.StationRequest;
import wooteco.subway.controller.dto.StationResponse;

@DisplayName("지하철 노선 관련 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

	private LineRequest lineRequest;

	@BeforeEach
	void setStation() {
		ExtractableResponse<Response> stationResponse1 = RestUtil.post(new StationRequest("강남역"));
		ExtractableResponse<Response> stationResponse2 = RestUtil.post(new StationRequest("역삼역"));
		lineRequest = new LineRequest(
			"신분당선", "bg-red-600",
			RestUtil.getIdFromStation(stationResponse1),
			RestUtil.getIdFromStation(stationResponse2),
			10);
	}

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		LineResponse lineResponse = RestUtil.toResponseDto(response, LineResponse.class);
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.header("Location")).isNotBlank(),
			() -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
			() -> assertThat(lineResponse.getColor()).isEqualTo("bg-red-600")
		);
	}

	@DisplayName("지하철 노선을 생성하면 역들을 응답으로 받는다.")
	@Test
	void createLineAndStations() {
		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		LineResponse lineResponse = RestUtil.toResponseDto(response, LineResponse.class);
		List<StationResponse> stations = lineResponse.getStations();

		assertThat(stations)
			.map(StationResponse::getName)
			.containsExactly("강남역", "역삼역");
	}

	@DisplayName("없는 역으로 노선을 생성하면 404 응답을 받는다.")
	@Test
	void createLineNoFoundStation() {
		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(new LineRequest("2호선", "red", 1L, 2L, 10))
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
	@Test
	void createLineWithDuplicateName() {
		// given
		RestUtil.post(lineRequest);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(lineRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철 노선 목록을 조회한다.")
	@Test
	void findAllLines() {
		// given
		ExtractableResponse<Response> createResponse1 = RestUtil.post(lineRequest);
		ExtractableResponse<Response> createResponse2 = RestUtil.post(
			new LineRequest("분당선", "bg-red-600",
				RestUtil.getIdFromStation(RestUtil.post(new StationRequest("잠실역"))),
				RestUtil.getIdFromStation(RestUtil.post(new StationRequest("선릉역"))),
				10)
		);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();

		// then
		List<Long> expectedLIneIds = Stream.of(
				RestUtil.getIdFromLine(createResponse1),
				RestUtil.getIdFromLine(createResponse2))
			.collect(Collectors.toList());
		List<Long> resultLineIds = RestUtil.getIdsFromLine(response);

		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(resultLineIds).containsAll(expectedLIneIds)
		);
	}

	@DisplayName("지하철 노선 하나를 조회한다.")
	@Test
	void findOneLine() {
		// given
		ExtractableResponse<Response> createResponse = RestUtil.post(lineRequest);

		// when
		Long createdId = RestUtil.getIdFromLine(createResponse);
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines/" + createdId)
			.then().log().all()
			.extract();

		// then
		LineResponse lineResponse = RestUtil.toResponseDto(response, LineResponse.class);

		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(lineResponse.getId()).isEqualTo(createdId),
			() -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
			() -> assertThat(lineResponse.getColor()).isEqualTo("bg-red-600")
		);
	}

	@DisplayName("없는 노선 조회 시 404 응답을 받는다.")
	@Test
	void findOneLineNotFound() {
		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines/" + 1)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> createResponse = RestUtil.post(lineRequest);

		// when
		Map<String, String> params2 = Map.of("name", "다른분당선", "color", "bg-red-600");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params2)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + RestUtil.getIdFromLine(createResponse))
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 삭제한다.")
	@Test
	void removeLine() {
		// given
		ExtractableResponse<Response> createResponse = RestUtil.post(lineRequest);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/" + RestUtil.getIdFromLine(createResponse))
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("없는 노선을 삭제하면 404 응답을 받는다.")
	@Test
	void removeLineNotFound() {
		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/" + 1)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}
}
