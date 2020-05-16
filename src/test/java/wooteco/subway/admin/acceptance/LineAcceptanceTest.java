package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.LineResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineAcceptanceTest {
	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	public static RequestSpecification given() {
		return RestAssured.given().
            log().all();
	}

	@Transactional
	@DisplayName("지하철 노선을 관리한다")
	@Test
	void manageLine() {
		// when
		createLine("신분당선");
		createLine("1호선");
		createLine("2호선");
		createLine("3호선");
		// then
		List<LineResponse> lines = getLines();
		assertThat(lines.size()).isEqualTo(4);

		// when
		LineResponse line = getLine(lines.get(0).getId());
		// then
		assertThat(line.getId()).isNotNull();
		assertThat(line.getName()).isNotNull();
		assertThat(line.getStartTime()).isNotNull();
		assertThat(line.getEndTime()).isNotNull();
		assertThat(line.getIntervalTime()).isNotNull();

		// when
		LocalTime startTime = LocalTime.of(8, 00);
		LocalTime endTime = LocalTime.of(22, 00);
		updateLine(line.getId(), startTime, endTime);
		//then
		LineResponse updatedLine = getLine(line.getId());
		assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
		assertThat(updatedLine.getEndTime()).isEqualTo(endTime);

		// when
		deleteLine(line.getId());
		// then
		List<LineResponse> linesAfterDelete = getLines();
		assertThat(linesAfterDelete.size()).isEqualTo(3);
	}

	@Test
	@DisplayName("중복된 노선명이 추가될 경우 에러 발생 확인")
	public void duplicateLineName() {
		// when
		createLine("신분당선");
		createLine("4호선");
		// then
		given().
			body(makeParam("4호선")).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
        when().
			post("/api/lines").
        then().
			log().all().
			statusCode(HttpStatus.BAD_REQUEST.value());
	}

	private Map<String, String> makeParam(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		params.put("color", "bg-gray-700");
		return params;
	}

	private LineResponse getLine(Long id) {
		return given().
            when().
                get("/api/lines/" + id).
			then().
                log().all().
			    extract().as(LineResponse.class);
	}

	private void createLine(String name) {
		Map<String, String> params = makeParam(name);

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
        when().
			post("/api/lines").
        then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
		Map<String, String> params = new HashMap<>();
		params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
        when().
			put("/api/lines/" + id).
        then().
			log().all().
			statusCode(HttpStatus.NO_CONTENT.value());
	}

	private List<LineResponse> getLines() {
		return
			given().
            when().
				get("/api/lines").
            then().
				log().all().
				extract().
				jsonPath().getList(".", LineResponse.class);
	}

	private void deleteLine(Long id) {
		given().
        when().
			delete("/api/lines/" + id).
        then().
			log().all();
	}
}
