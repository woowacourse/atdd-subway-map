package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.StationRequest;
import wooteco.subway.controller.dto.StationResponse;

@DisplayName("지하철역 관련 기능 인수 테스트")
public class StationAcceptanceTest extends AcceptanceTest {

	private final StationRequest stationRequest = new StationRequest("강남역");

	@DisplayName("지하철역을 생성한다.")
	@Test
	void createStation() {
		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(stationRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then().log().all()
			.extract();

		// then
		StationResponse stationResponse = RestUtil.toResponseDto(response, StationResponse.class);

		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(response.header("Location")).isNotBlank(),
			() -> assertThat(RestUtil.getIdFromStation(response)).isNotNull(),
			() -> assertThat(stationResponse.getName()).isEqualTo("강남역")
		);
	}

	@DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 400 응답을 받는다.")
	@Test
	void createStationWithDuplicateName() {
		// given
		RestUtil.post(stationRequest);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(stationRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/stations")
			.then()
			.log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("지하철역을 조회한다.")
	@Test
	void getStations() {
		/// given
		ExtractableResponse<Response> createResponse1 = RestUtil.post(stationRequest);
		ExtractableResponse<Response> createResponse2 = RestUtil.post(new StationRequest("역삼역"));

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.get("/stations")
			.then().log().all()
			.extract();

		// then
		List<Long>expectedLineIds = extractExpectedIds(createResponse1, createResponse2);
		List<Long> resultLineIds = RestUtil.getIdsFromStation(response);
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(resultLineIds).containsAll(expectedLineIds)
		);
	}

	private List<Long> extractExpectedIds(ExtractableResponse<Response> createResponse1,
		ExtractableResponse<Response> createResponse2) {
		return Stream.of(createResponse1, createResponse2)
			.map(it -> Long.parseLong(it.header("Location").split("/")[2]))
			.collect(Collectors.toList());
	}

	@DisplayName("지하철역을 제거한다.")
	@Test
	void deleteStation() {
		// given
		ExtractableResponse<Response> createResponse = RestUtil.post(stationRequest);

		// when
		String uri = createResponse.header("Location");
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete(uri)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
	}

	@DisplayName("없는 지하철역을 제거하면 404 응답을 받는다.")
	@Test
	void deleteStationBadRequest() {
		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete("/stations/1")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
	}

	@DisplayName("구간에 등록되어 있는 역을 지우면 400 응답을 받는다.")
	@Test
	void deleteStationBadRequestBySection() {
		// given
		Long upStationId = RestUtil.getIdFromStation(RestUtil.post(new StationRequest("강남역")));
		Long downStationId = RestUtil.getIdFromStation(RestUtil.post(new StationRequest("역삼역")));
		RestUtil.post(new LineRequest("2호선", "red", upStationId, downStationId, 10));

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.when()
			.delete("/stations/" + upStationId)
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
