package wooteco.subway.admin.acceptance;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

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
		StationResponse firstStation = createStation("잠실");
		StationResponse secondStation = createStation("잠실새내");
		LineResponse line = createLine("2호선");

		addLineStation(line.getId(), null, firstStation.getId());

		//createLineStation(1L, null, 1L);
	}

	private StationResponse createStation(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);

		return given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/stations").
			then().
			log().all().
			extract().as(StationResponse.class);
	}

	private LineResponse createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("title", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("bgColor", "bg-red-400");
		params.put("intervalTime", "10");

		return given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lines").
			then().
			log().all().extract().as(LineResponse.class);
	}

	private void addLineStation(Long lineId, Long preStationId, Long stationId) {
		Map<String, String> params = new HashMap<>();
		params.put("line", String.valueOf(lineId));
		params.put("preStationId", String.valueOf(preStationId));
		params.put("stationId", String.valueOf(stationId));
		params.put("distance", "1000");
		params.put("duration", "5");

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lineStation/" + lineId).
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}
}
