package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

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
		return RestAssured.given().log().all();
	}

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


		assertThatThrownBy(() -> createLine("3호선"));

		// when
		LocalTime startTime = LocalTime.of(8, 0);
		LocalTime endTime = LocalTime.of(22, 0);
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
		assertThat(linesAfterDelete.stream().map(LineResponse::getName).collect(Collectors.toList()))
			.containsExactly("1호선", "2호선", "3호선");

		assertThatThrownBy(() -> deleteLine(Long.MAX_VALUE));
	}

	private LineResponse getLine(Long id) {
		return given().
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			get("/lines/" + id).
			then().
			log().all().
			extract().as(LineResponse.class);
	}

	private void createLine(String name) {
		Map<String, String> params = new HashMap<>();
		params.put("name", name);
		params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		params.put("bgColor", "bg-yellow-800");

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			post("/lines").
			then().
			log().all().
			statusCode(HttpStatus.CREATED.value());
	}

	private void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
		Map<String, String> params = new HashMap<>();
		params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
		params.put("intervalTime", "10");
		params.put("name", "100호선");
		params.put("bgColor", "bg-yellow-800");

		given().
			body(params).
			contentType(MediaType.APPLICATION_JSON_VALUE).
			accept(MediaType.APPLICATION_JSON_VALUE).
			when().
			put("/lines/" + id).
			then().
			log().all().
			statusCode(HttpStatus.OK.value());
	}

	private List<LineResponse> getLines() {
		return
			given().
				accept(MediaType.APPLICATION_JSON_VALUE).
				when().
				get("/lines").
				then().
				log().all().
				extract().
				jsonPath().getList(".", LineResponse.class);
	}

	private void deleteLine(Long id) {
		given().
			when().
			delete("/lines/" + id).
			then().
			log().all().
			statusCode(HttpStatus.NO_CONTENT.value());
	}
}
