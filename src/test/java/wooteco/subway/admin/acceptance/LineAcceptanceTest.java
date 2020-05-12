package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.admin.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

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
}
