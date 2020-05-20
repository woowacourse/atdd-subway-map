package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.dto.EdgeDeleteRequest;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class EdgeAcceptanceTest {
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
		// * Given 지하철역이 여러 개 추가되어있다.
		// * And 지하철 노선이 추가되어있다.
		Long station1 = createStation("선릉역");
		Long station2 = createStation("강남역");
		Long line = createLine("2호선");

		EdgeCreateRequest edgeCreateRequest = new EdgeCreateRequest(station1,
			station2, 10, 10);

		// * When 지하철 노선에 지하철역을 등록하는 요청을 한다.
		// * Then 지하철역이 노선에 추가 되었다.
		given()
			.body(edgeCreateRequest)
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.when()
			.post("/lines/{id}/edges", line)
			.then()
			.log().all()
			.statusCode(HttpStatus.CREATED.value());

		// * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
		// * Then 지하철역 목록을 응답 받는다.
		// * And 새로 추가한 지하철역을 목록에서 찾는다.
		List<LineResponse> lineResponses = given()
			.when()
			.get("/lines")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.jsonPath()
			.getList(".", LineResponse.class);

		List<String> stationNames = lineResponses.get(0).getStations().stream()
			.map(StationResponse::getName)
			.collect(Collectors.toList());

		assertThat(stationNames.size()).isEqualTo(2);
		assertThat(stationNames).contains("선릉역");
		assertThat(stationNames).contains("강남역");

		// * When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
		// * Then 지하철역이 노선에서 제거 되었다.
		EdgeDeleteRequest edgeDeleteRequest = new EdgeDeleteRequest(station1,
			station2);

		given()
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON_VALUE)
			.body(edgeDeleteRequest)
			.when()
			.delete("/lines/{id}/edges", line)
			.then()
			.log().all()
			.statusCode(HttpStatus.NO_CONTENT.value());

		// * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
		// * Then 지하철역 목록을 응답 받는다.
		// * And 제외한 지하철역이 목록에 존재하지 않는다.
		List<LineResponse> lineResponsesAfterDelete = given()
			.when()
			.get("/lines")
			.then()
			.log().all()
			.statusCode(HttpStatus.OK.value())
			.extract()
			.jsonPath()
			.getList(".", LineResponse.class);

		List<Long> stationIds = lineResponsesAfterDelete.get(0).getStations().stream()
			.map(StationResponse::getId)
			.collect(Collectors.toList());

		assertThat(stationIds.size()).isEqualTo(1);
		assertThat(stationIds.get(0)).isEqualTo(station1);
	}

	private Long createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime",
			LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime",
			LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		params.put("bgColor", "bg-red-200");

		return given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lines").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value())
			.extract().as(Long.class);
	}

	private Long createStation(String name) {
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
			statusCode(HttpStatus.CREATED.value())
			.extract().as(Long.class);
	}

}
