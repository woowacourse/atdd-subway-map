package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import wooteco.subway.admin.dto.LineResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineAcceptanceTest {
	@LocalServerPort
	int port;
	private AcceptanceTest acceptanceTest = new AcceptanceTest();

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@DisplayName("지하철 노선을 관리한다")
	@Test
	void manageLine() {
		// when
		acceptanceTest.createLine("신분당선");
		acceptanceTest.createLine("1호선");
		acceptanceTest.createLine("2호선");
		acceptanceTest.createLine("3호선");

		// then
		List<LineResponse> lines = acceptanceTest.getLines();
		assertThat(lines.size()).isEqualTo(4);

		// when
		LineResponse line = acceptanceTest.getLine(lines.get(0).getId());
		// then
		assertThat(line.getId()).isNotNull();
		assertThat(line.getName()).isNotNull();
		assertThat(line.getStartTime()).isNotNull();
		assertThat(line.getEndTime()).isNotNull();
		assertThat(line.getIntervalTime()).isNotNull();

		// when
		LocalTime startTime = LocalTime.of(8, 0);
		LocalTime endTime = LocalTime.of(22, 0);
		acceptanceTest.updateLine(line.getId(), line.getName(), startTime, endTime);
		//then
		LineResponse updatedLine = acceptanceTest.getLine(line.getId());
		assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
		assertThat(updatedLine.getEndTime()).isEqualTo(endTime);

		// when
		acceptanceTest.deleteLine(line.getId());
		// then
		List<LineResponse> linesAfterDelete = acceptanceTest.getLines();
		assertThat(linesAfterDelete.size()).isEqualTo(3);
	}

}
