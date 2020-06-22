package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.dto.LineResponse;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        // 지하철 노선을 등록하는 요청을 한다
        createLine("신분당선");
        createLine("1호선");
        createLine("2호선");
        createLine("3호선");
        // 지하철 노선의 목록 조회 요청을 한다
        List<LineResponse> lines = getLines();
        // 지하철 노선이 등록되었다
        assertThat(lines.size()).isEqualTo(4);

        // 지하철 노선을 id로 찾는다
        LineResponse line = getLine(lines.get(0).getId());
        // id에 해당하는 노선이 존재한다
        assertThat(line.getId()).isNotNull();
        assertThat(line.getName()).isNotNull();
        assertThat(line.getStartTime()).isNotNull();
        assertThat(line.getEndTime()).isNotNull();
        assertThat(line.getIntervalTime()).isNotNull();

        // 노선의 시간을 변경하는 요청을 한다
        LocalTime startTime = LocalTime.of(8, 00);
        LocalTime endTime = LocalTime.of(22, 00);
        updateLine(line.getId(), startTime, endTime);
        //해당 노선의 시간이 변경되었다
        LineResponse updatedLine = getLine(line.getId());
        assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
        assertThat(updatedLine.getEndTime()).isEqualTo(endTime);

        // 노선을 지우는 요청을 한다
        deleteLine(line.getId());
        // 지하철 노선이 제거되었다
        List<LineResponse> linesAfterDelete = getLines();
        assertThat(linesAfterDelete.size()).isEqualTo(3);
    }
}
