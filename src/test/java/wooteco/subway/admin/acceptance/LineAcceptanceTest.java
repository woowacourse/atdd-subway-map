package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        updateLine(line, startTime, endTime);
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

    private LineResponse getLine(Long id) {
        return given()
                .when().
                        get("/lines/" + id).
                then().
                        log().all().
                        extract().as(LineResponse.class);
    }


    private void updateLine(LineResponse lineResponse, LocalTime startTime, LocalTime endTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", lineResponse.getId());
        params.put("color", lineResponse.getColor());
        params.put("startTime", startTime.toString().substring(0, 5));
        params.put("endTime", endTime.toString().substring(0, 5));
        params.put("intervalTime", 10);

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                put("/lines/" + lineResponse.getId()).
        then().
                log().all().
                statusCode(HttpStatus.OK.value());
    }

    private List<LineResponse> getLines() {
        return given()
                .when().
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
                log().all();
    }
}
