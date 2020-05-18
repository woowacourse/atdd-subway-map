package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import wooteco.subway.admin.acceptance.handler.LineHandler;
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

    @Autowired
    private LineHandler lineHandler;
    // TODO: 2020-05-08 테스트용 클래스를 IOC 컨테이너로 관리해도 되는지?

    @DisplayName("지하철 노선을 관리한다")
    @Test
    void manageLine() {
        // when
        lineHandler.createLine("신분당선", "blue");
        lineHandler.createLine("1호선", "pink");
        lineHandler.createLine("2호선", "yellow");
        lineHandler.createLine("3호선", "green");
        // then
        List<LineResponse> lines = lineHandler.getLines();
        assertThat(lines.size()).isEqualTo(4);

        // when
        LineResponse line = lineHandler.getLine(lines.get(0).getId());
        // then
        assertThat(line.getId()).isNotNull();
        assertThat(line.getName()).isNotNull();
        assertThat(line.getStartTime()).isNotNull();
        assertThat(line.getEndTime()).isNotNull();
        assertThat(line.getIntervalTime()).isNotNull();

        // when
        LocalTime startTime = LocalTime.of(8, 00);
        LocalTime endTime = LocalTime.of(22, 00);
        lineHandler.updateLine(line.getId(), startTime, endTime);
        //then
        LineResponse updatedLine = lineHandler.getLine(line.getId());
        assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
        assertThat(updatedLine.getEndTime()).isEqualTo(endTime);

        // when
        lineHandler.deleteLine(line.getId());
        // then
        List<LineResponse> linesAfterDelete = lineHandler.getLines();
        assertThat(linesAfterDelete.size()).isEqualTo(3);
    }
}
