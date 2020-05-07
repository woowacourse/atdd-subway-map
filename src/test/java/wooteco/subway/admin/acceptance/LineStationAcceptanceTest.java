package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;

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
		// Given 지하철역이 여러 개 추가되어있다.
		// And 지하철 노선이 추가되어있다.
		stationSetting();
		lineSetting();

		Line line;
		Station station;

		// When 지하철 노선에 지하철역을 등록하는 요청을 한다.
		addStationToLine(line, station);
		// Then 지하철역이 노선에 추가 되었다.
		assertThat(line.getStations).isNotNull();

		// When 지하철 노선의 지하철역 목록 조회 요청을 한다.
		List<Station> stations = findAllStation(station);
		// Then 지하철역 목록을 응답 받는다.
		assertThat(stations).isNotNull();
		// And 새로 추가한 지하철역을 목록에서 찾는다.
		assertThat(stations).contains(station);

		// When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
		deleteStation(line, station);
		// Then 지하철역이 노선에서 제거 되었다.
		assertThat(stations).doseNotContain(station);

		// When 지하철 노선의 지하철역 목록 조회 요청을 한다.
		stations = findAllStation(line);
		// Then 지하철역 목록을 응답 받는다.
		assertThat(stations).isNotNull();
		// And 제외한 지하철역이 목록에 존재하지 않는다.
		assertThat(stations).doseNotContain(station);
	}
}
