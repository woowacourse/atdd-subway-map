package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.service.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {
	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	public static RequestSpecification given() {
		return RestAssured.given().log().all();
	}

	/**
	 *     Given 지하철역이 여러 개 추가되어있다.
	 *     And 지하철 노선이 여러 개 추가되어있다.
	 *
	 *     When 지하철 노선에 두 지하철역을 통해 구간을 등록하는 요청을 한다.
	 *     Then 지하철역이 추가 되었다.
	 *
	 *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
	 *     Then 지하철역 목록을 응답 받는다.
	 *     And 새로 추가한 지하철역을 목록에서 찾는다.
	 *
	 *     When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
	 *     Then 지하철역이 노선에서 제거 되었다.
	 *
	 *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
	 *     Then 지하철역 목록을 응답 받는다.
	 *     And 제외한 지하철역이 목록에 존재하지 않는다.
	 *
	 *
	 *     해당 테스트는 기존에 Mock 서버를 제거함으로 인해 현재
	 *     통과되지 않습니다.
	 */
	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
	@Test
	void manageLineStation() {
		addLineStation(1L, null, 0L);
		addLineStation(1L, 0L, 1L);
		LineResponse line = getLine(1L);
		assertThat(line.getStations()).hasSize(2);

		List<String> stationIds = getStations(line.getId()).stream()
		                                                   .map(StationResponse::getName)
		                                                   .collect(Collectors.toList());
		assertThat(stationIds).contains("잠실", "잠실나루");

		deleteLineStation(1L, 0L);
		line = getLine(1L);
		assertThat(line.getStations()).hasSize(1);
	}

	private LineResponse getLine(Long id) {
		return given()
			.when()
			.get("/lines/" + id)
			.then()
			.log().all()
			.extract().as(LineResponse.class);

	}

	private List<StationResponse> getStations(Long id) {
		return given()
			.when()
			.get("/lines/" + id + "/stations")
			.then()
			.log().all()
			.extract()
			.jsonPath().getList(".", StationResponse.class);
	}

	private void addLineStation(Long lineId, Long preStationId, Long stationId) {
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

	private void deleteLineStation(Long lineId, Long stationId) {
		given()
			.when()
			.delete("/lines/" + lineId + "/stations/" + stationId)
			.then()
			.log().all();
	}
}
