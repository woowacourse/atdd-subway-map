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
import wooteco.subway.admin.line.service.dto.line.LineResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineAcceptanceTest extends AcceptanceTest {

	@LocalServerPort
	int port;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@DisplayName("지하철 노선을 관리한다")
	@Test
	void manageLine() {
		// when 노선을 생성한다.
		createLine("신분당선");
		createLine("1호선");
		createLine("2호선");
		createLine("3호선");
		// then 노선이 생성되었고 총 라인의 수가 4개이다.
		List<LineResponse> lines = getLines();
		assertThat(lines.size()).isEqualTo(4);

		// when 첫번째 노선인 신분당선을 가져온다.
		LineResponse line = getLine(lines.get(0).getId());
		// then 노선이 정상적으로 저장되었다.
		assertThat(line.getId()).isNotNull();
		assertThat(line.getName()).isNotNull();
		assertThat(line.getStartTime()).isNotNull();
		assertThat(line.getEndTime()).isNotNull();
		assertThat(line.getIntervalTime()).isNotNull();

		// when 첫번째 노선인 신분당선의 시작시간과 종료시간을 수정한다.
		LocalTime startTime = LocalTime.of(8, 00);
		LocalTime endTime = LocalTime.of(22, 00);
		updateLine(line.getId(), startTime, endTime);
		//then 변경 사항이 정상적으로 수정되었다.
		LineResponse updatedLine = getLine(line.getId());
		assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
		assertThat(updatedLine.getEndTime()).isEqualTo(endTime);

		// when 첫번재 노선인 신분당선을 삭제하였다.
		deleteLine(line.getId());
		// then 노선에서 삭제가 되어서 3개의 노선만 존재한다.
		List<LineResponse> linesAfterDelete = getLines();
		assertThat(linesAfterDelete.size()).isEqualTo(3);

		// when 기존에 존재하는 노선과 중복되는 노선을 저장하였다.
		createDuplicatedLine("1호선");
		// then Bad_Request가 발생하고 기존과 변함없이 3개의 노선만 존재한다.
		lines = getLines();
		assertThat(lines).hasSize(3);
	}

}
