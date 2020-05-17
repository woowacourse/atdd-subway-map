package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import wooteco.subway.admin.line.service.dto.line.LineResponse;
import wooteco.subway.admin.station.service.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest extends AcceptanceTest {

	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
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
		// Given 지하철 역이 여러 개 추가되어 있다.
		Long jamsilId = createStation("잠실역");
		Long jonghapStadiumId = createStation("종합운동장역");
		Long sunlengId = createStation("선릉역");
		Long gangnamId = createStation("강남역");
		// Given 지하철 노선이 여러 개 추가되어 있다.
		Long line1Id = createLine("1호선");
		Long line2Id = createLine("2호선");

		// When 지하철 노선에 두 지하철역을 통해 구간을 등록하는 요청을 한다.
		addLineStation(line2Id, null, jamsilId);
		addLineStation(line2Id, jamsilId, jonghapStadiumId);
		// Then 지하철역이 추가 되었다.
		LineResponse line = getLine(line2Id);
		assertThat(line.getStations()).hasSize(2);

		// When 지하철 노선의 지하철역 목록 조회 요청을 한다.
		List<String> stationNames = getStation(line.getId())
			.stream()
			.map(StationResponse::getName)
			.collect(Collectors.toList());
		// Then 지하철역 목록을 응답 받고 새로 추가된 지하철역을 목록에서 찾는다.
		assertThat(stationNames).contains("잠실역", "종합운동장역");

		// When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
		deleteLineStation(line2Id, jamsilId);
		line = getLine(line2Id);
		// Then 지하철역이 노선에서 제거 되어서 지하철 역의 개수가 1개이다.
		assertThat(line.getStations()).hasSize(1);

		// When 지하철 노선의 지하철역 목록 조회 요청을 한다.
		stationNames = getStation(line.getId())
			.stream()
			.map(StationResponse::getName)
			.collect(Collectors.toList());
		// Then 제외한 지하철역이 목록에 존재하지 않는다.
		assertThat(stationNames).doesNotContain("잠실역");
	}

}
