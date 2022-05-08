package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.LineRequest;
import wooteco.subway.controller.dto.SectionRequest;
import wooteco.subway.controller.dto.StationRequest;

@DisplayName("지하철 구간 관련 인수 테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

	private Long upStationId;
	private Long downStationId;
	private Long lineId;

	@BeforeEach
	void init() {
		ExtractableResponse<Response> stationResponse1 = RestUtil.post(new StationRequest("강남역"));
		ExtractableResponse<Response> stationResponse2 = RestUtil.post(new StationRequest("역삼역"));
		LineRequest lineRequest = new LineRequest(
			"신분당선", "bg-red-600",
			RestUtil.getIdFromStation(stationResponse1),
			RestUtil.getIdFromStation(stationResponse2),
			10);
		ExtractableResponse<Response> lineResponse = RestUtil.post(lineRequest);

		upStationId = RestUtil.getIdFromStation(stationResponse1);
		downStationId = RestUtil.getIdFromStation(stationResponse2);
		lineId = RestUtil.getIdFromLine(lineResponse);
	}

	@DisplayName("지하철 구간을 등록한다.")
	@Test
	void addSection() {
		// given
		ExtractableResponse<Response> stationResponse = RestUtil.post(new StationRequest("선릉역"));
		Long newStationId = RestUtil.getIdFromStation(stationResponse);
		SectionRequest sectionRequest = new SectionRequest(downStationId, newStationId, 10);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("지하철 구간을 추가할 수 없으면 400 예외가 발생한다.")
	@Test
	void addSectionBadRequest() {
		// given
		ExtractableResponse<Response> stationResponse = RestUtil.post(new StationRequest("선릉역"));
		Long newStationId = RestUtil.getIdFromStation(stationResponse);
		SectionRequest sectionRequest = new SectionRequest(upStationId, newStationId, 10);

		// when
		ExtractableResponse<Response> response = RestAssured.given().log().all()
			.body(sectionRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/" + lineId + "/sections")
			.then().log().all()
			.extract();

		// then
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
