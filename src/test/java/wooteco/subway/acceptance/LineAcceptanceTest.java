package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.LineResponse;
import wooteco.subway.controller.dto.StationRequest;

@DisplayName("지하철 노선 관련 인수 테스트")
@Sql(scripts = "classpath:/delete.sql",
	executionPhase = ExecutionPhase.AFTER_TEST_METHOD
)
public class LineAcceptanceTest extends AcceptanceTest {

	private LineRequest lineRequest;

	@BeforeEach
	void setStation() {
		ExtractableResponse<Response> stationResponse1 = RestAssuredUtil.post(new StationRequest("강남역"));
		ExtractableResponse<Response> stationResponse2 = RestAssuredUtil.post(new StationRequest("역삼역"));
		lineRequest = new LineRequest(
			"신분당선", "bg-red-600",
			RestAssuredUtil.getIdFromStation(stationResponse1),
			RestAssuredUtil.getIdFromStation(stationResponse2),
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
		assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(response.header("Location")).isNotBlank();
		assertThat(extractName(response)).isEqualTo("신분당선");
	}

	private String extractName(ExtractableResponse<Response> response) {
		return response.body()
			.jsonPath()
			.getObject(".", LineResponse.class)
			.getName();
	}

	@DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
	@Test
	void createLineWithDuplicateName() {
		// given
		RestAssuredUtil.post(lineRequest);

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

	@DisplayName("지하철노선을 조회한다.")
	@Test
	void getLines() {
		// given
		ExtractableResponse<Response> createResponse1 = RestAssuredUtil.post(lineRequest);
		ExtractableResponse<Response> createResponse2 = RestAssuredUtil.post(
			new LineRequest("분당선", "bg-red-600", 3L, 4L, 10)
		);

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
		ExtractableResponse<Response> createResponse = RestAssuredUtil.post(lineRequest);

		// when
		Long createdId = RestAssuredUtil.getIdFromLine(createResponse);
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.get("/lines/" + RestAssuredUtil.getIdFromLine(createResponse))
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		Long expectedId = RestAssuredUtil.getIdFromLine(response);
		assertThat(expectedId).isEqualTo(createdId);
	}

	@DisplayName("지하철 노선을 수정한다.")
	@Test
	void updateLine() {
		// given
		ExtractableResponse<Response> createResponse = RestAssuredUtil.post(lineRequest);

		// when
		Map<String, String> params2 = Map.of("name", "다른분당선", "color", "bg-red-600");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(params2)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.put("/lines/" + RestAssuredUtil.getIdFromLine(createResponse))
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 노선을 삭제한다.")
	@Test
	void removeLine() {
		// given
		ExtractableResponse<Response> createResponse = RestAssuredUtil.post(lineRequest);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.delete("/lines/" + RestAssuredUtil.getIdFromLine(createResponse))
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}
}
