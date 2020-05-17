package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.admin.acceptance.Request.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {
	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@DisplayName("지하철 노선에서의 지하철역 추가 / 삭제 / 조회 테스트")
	@TestFactory
	public List<DynamicTest> lineStationScenarioTest() {
		DynamicTest createLineTest = DynamicTest.dynamicTest("지하철 라인을 생성한다.", () -> {
			//Given 지하철 노선을 여러개 추가한다.
			createLine("1호선");
			createLine("2호선");

			//When 지하철 노선목록 조회요청한다.
			List<LineResponse> lines = getLines();

			//Then 추가한 지하철노선이 잘 들어있다.
			List<String> lineNames = lines.stream()
				.map(LineResponse::getTitle)
				.collect(Collectors.toList());
			assertThat(lineNames).contains("1호선", "2호선");
		});

		DynamicTest createStationTest = DynamicTest.dynamicTest("지하철 역을 생성한다.", () -> {
			//Given 지하철역을 여러개 추가한다.
			createStation("신촌");
			createStation("잠실");

			//When 지하철 역목록 조회요청한다.
			List<StationResponse> stations = getStations();

			//Then 추가한 지하철역이 잘 들어있다.
			List<String> stationNames = stations.stream()
				.map(StationResponse::getName)
				.collect(Collectors.toList());
			assertThat(stationNames).contains("잠실", "신촌");
		});

		DynamicTest createLineStationTest = DynamicTest.dynamicTest("지하철 노선에 두 지하철역을 통해 구간을 등록하는 요청을 한다.", () -> {
			//before 구간 등록 이전에는 그 지하철 노선엔 지하철 역이 없다.
			LineResponse line = getLine(1L);
			assertThat(line.getStations()).hasSize(0);

			//When 지하철 노선에 두 지하철역을 통해 구간을 등록하는 요청을 한다.
			addLineStation(1L, null, 1L);
			addLineStation(1L, 1L, 2L);

			//Then 지하철역이 추가 되었다.
			line = getLine(1L);
			assertThat(line.getStations()).hasSize(2);
		});

		DynamicTest findLineWithStationTest = DynamicTest.dynamicTest("지하철 노선에서 새로 추가한 지하철이 있는지 확인한다.", () -> {
			//When 지하철 노선의 지하철역 목록 조회 요청을 한다.
			List<String> stationNames = getStations(1L).stream()
				.map(StationResponse::getName)
				.collect(Collectors.toList());

			//Then 새로 추가한 지하철역을 응답에서 찾는다.
			assertThat(stationNames).contains("잠실", "신촌");
		});

		DynamicTest deleteLineTest = DynamicTest.dynamicTest("지하철 노선에서 특정 노선을 지우는 요청을 보내고 확인한다.", () -> {
			//before 지하철역 제외요청이전에는 지하철역이 잘 있다.
			assertThat(getLine(1L).getStations()).hasSize(2);

			//When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
			deleteLineStation(1L, 1L);

			//Then 지하철역이 노선에서 제거 되었다.
			assertThat(getLine(1L).getStations()).hasSize(1);
		});

		DynamicTest deleteLineAndFindStationTest = DynamicTest.dynamicTest("지하철 노선에서 특정 노선이 잘 지워졌는지 확인한다.", () -> {
			//When 지하철 노선의 지하철역 목록 조회 요청을 한다.
			List<String> stationNames = getStations(1L).stream()
				.map(StationResponse::getName)
				.collect(Collectors.toList());

			//Then 제외한 지하철역이 응답에 존재하지 않는다.
			assertThat(stationNames).doesNotContain("신촌");

			//And 제외하지 않은 역은 존재한다.
			assertThat(stationNames).contains("잠실");
		});

		return Arrays.asList(createLineTest, createStationTest, createLineStationTest, findLineWithStationTest,
			deleteLineTest, deleteLineAndFindStationTest);
	}
}
