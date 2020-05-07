package wooteco.subway.admin.acceptance;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.Set;

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
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {
	@LocalServerPort
	int port;

	public static RequestSpecification given() {
		return RestAssured.given().log().all();
	}

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	/**
	 *     Given 지하철역이 여러 개 추가되어있다.
	 *     And 지하철 노선이 추가되어있다.
	 *
	 *     When 지하철 노선에 지하철역을 등록하는 요청을 한다.
	 *     Then 지하철역이 노선에 추가 되었다.
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
	 */
	@DisplayName("지하철 노선에서 지하철역 추가 / 제외")
	@Test
	void manageLineStation() {
		addLineStation(1L, null, 1L, 0, 0);
		addLineStation(1L, 1L, 2L, 3, 3);
		addLineStation(1L, 2L, 3L, 3, 3);
		addLineStation(1L, 3L, 4L, 3, 3);

		Set<Station> stations = findLineWithStationsById(1L).getStations();
		assertThat(stations).hasSize(4);

		removeLineStation(1L, 1L);
		stations = findLineWithStationsById(1L).getStations();
		assertThat(stations).hasSize(3);
	}

	private void removeLineStation(final Long lineId, final long stationId) {
		when()
			.delete("/lines/" + lineId + "/stations/" + stationId)
			.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());
	}

	private LineResponse findLineWithStationsById(Long id) {
		return given().when().
			get("/lines/" + id + "/stations").
			then().
			log().all().
			extract().as(LineResponse.class);
	}

	private void addLineStation(Long lineId, Long preStationId, Long stationId, int distance, int duration) {
		LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(preStationId, stationId,
			distance, duration);

		given().
			body(lineStationCreateRequest).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lines/" + lineId + "/stations").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}
}
