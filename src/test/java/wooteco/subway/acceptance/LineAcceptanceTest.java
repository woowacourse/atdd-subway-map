package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

	@DisplayName("지하철 노선을 생성한다.")
	@Test
	void createLine() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", "신분당선");
		params.put("color", "bg-red-600");

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
	}

	@DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
	@Test
	void createLineWithDuplicateName() {
		// given
		Map<String, String> params = new HashMap<>();
		params.put("name", "신분당선");
		params.put("color", "bg-red-600");
		RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철노선을 조회한다.")
	@Test
	void getLines() {
		// given
		Map<String, String> params1 = new HashMap<>();
		params1.put("name", "신분당선");
		params1.put("color", "bg-red-600");
		ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
			.body(params1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		Map<String, String> params2 = new HashMap<>();
		params2.put("name", "분당선");
		params2.put("color", "bg-red-600");
		ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
			.body(params2)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/lines")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		List<Long> expectedLIneIds = Stream.of(createResponse1, createResponse2)
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
		List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
			.map(LineResponse::getId)
			.collect(Collectors.toList());
		assertThat(resultLineIds).containsAll(expectedLIneIds);
	}

	@DisplayName("지하철 노선을 조회한다.")
	@Test
	void findLine() {
		// given
		Map<String, String> params1 = new HashMap<>();
		params1.put("name", "신분당선");
		params1.put("color", "bg-red-600");
		ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
			.body(params1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		Long createdId = createResponse.jsonPath()
			.getObject(".", LineResponse.class)
			.getId();

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines/" + createdId)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		Long expectedId = response.jsonPath()
			.getObject(".", LineResponse.class)
			.getId();
		assertThat(expectedId).isEqualTo(createdId);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		Map<String, String> params1 = new HashMap<>();
		params1.put("name", "신분당선");
		params1.put("color", "bg-red-600");
		ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
			.body(params1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		Long createdId = createResponse.jsonPath()
			.getObject(".", LineResponse.class)
			.getId();

		// when
		Map<String, String> params2 = new HashMap<>();
		params2.put("name", "다른분당선");
		params2.put("color", "bg-red-600");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params2)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + createdId)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 삭제한다.")
	@Test
	void removeLine() {
		// given
		Map<String, String> params1 = new HashMap<>();
		params1.put("name", "신분당선");
		params1.put("color", "bg-red-600");
		ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
			.body(params1)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines")
			.then().log().all()
			.extract();

		Long createdId = createResponse.jsonPath()
			.getObject(".", LineResponse.class)
			.getId();

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/" + createdId)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
