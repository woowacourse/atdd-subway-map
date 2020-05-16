package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineWithStationsResponse;
import wooteco.subway.admin.dto.StationResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // @formatter:off

		// Given 지하철역이 여러 개 추가되어있다.
		StationResponse stationResponse1 = createStation("신림");
		StationResponse stationResponse2 = createStation("설입");
		StationResponse stationResponse3 = createStation("사당");
		StationResponse stationResponse4 = createStation("서초");

		// And 지하철 노선이 추가되어있다.
		LineWithStationsResponse lineWithStationsResponse = createLine("2호선");

		// When 지하철 노선에 지하철역을 등록하는 요청을 한다.
		addStationOnLine(lineWithStationsResponse.getId(),"", stationResponse1.getName());
		addStationOnLine(lineWithStationsResponse.getId(),stationResponse1.getName(), stationResponse2.getName());
		addStationOnLine(lineWithStationsResponse.getId(),stationResponse2.getName(), stationResponse3.getName());
		addStationOnLine(lineWithStationsResponse.getId(),stationResponse3.getName(), stationResponse4.getName());

		// Then 지하철역이 노선에 추가 되었다.
		LineWithStationsResponse persistLine  = getLineBy(lineWithStationsResponse.getId());
		assertEquals(persistLine.getStations().size(), 4);

		// When 지하철 노선의 지하철역 목록 조회 요청을 한다.
		// Then 지하철역 목록을 응답 받는다.
		List<Station> lineStationResponses = new ArrayList(persistLine.getStations());

		// And 새로 추가한 지하철역을 목록에서 찾는다.
		assertEquals(lineStationResponses.get(0).getId(), stationResponse1.getId());
		assertEquals(lineStationResponses.get(1).getId(), stationResponse2.getId());
		assertEquals(lineStationResponses.get(2).getId(), stationResponse3.getId());
		assertEquals(lineStationResponses.get(3).getId(), stationResponse4.getId());

		// When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
		deleteStationBy(lineWithStationsResponse.getId(), stationResponse4.getId());

		// Then 지하철역이 노선에서 제거 되었다.
		persistLine = getLineBy(lineWithStationsResponse.getId());
		assertEquals(persistLine.getStations().size(), 4);
		// When 지하철 노선의 지하철역 목록 조회 요청을 한다.
		// Then 지하철역 목록을 응답 받는다.
		// And 제외한 지하철역이 목록에 존재하지 않는다.
		assertThat(persistLine.getStations().get(0).getId()).isNotEqualTo(stationResponse4.getId());
	}

	private void deleteStationBy(Long lineId, Long stationId) {
		given().
				when()
				.delete("/lines/" + lineId +"/line-stations/" + stationId)
				.then()
				.log().all();
	}

	private LineWithStationsResponse getLineBy(Long lineId) {
		return given().
				when().
					get("/lines/" + lineId).
				then().
					log().all().
					extract().as(LineWithStationsResponse.class);
	}

	private void addStationOnLine(Long lineId, String preStationName, String stationName) {
		Map<String, String> params = new HashMap<>();
		params.put("lineId", String.valueOf(lineId));
		params.put("preStationName", preStationName);
		params.put("stationName", stationName);

		given().
				body(params).
				contentType(MediaType.APPLICATION_JSON_VALUE).
				accept(MediaType.APPLICATION_JSON_VALUE).
			when().
				post("/lines/line-stations").
			then().
				log().all();
	}

	private LineWithStationsResponse createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		params.put("lineColor", "bg-pink-700");

		return given().
					body(params).
					contentType(MediaType.APPLICATION_JSON_VALUE).
					accept(MediaType.APPLICATION_JSON_VALUE).
				when().
					post("/lines").
				then().
					log().all().
					extract().as(LineWithStationsResponse.class);
	}

	private StationResponse createStation(String stationName) {
		Map<String, String> params = new HashMap<>();
		params.put("name", stationName);

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
	// @formatter:on
}
