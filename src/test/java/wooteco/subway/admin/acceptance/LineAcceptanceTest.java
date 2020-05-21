package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.dto.service.response.LineServiceResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    // @formatter:off

    @DisplayName("지하철 노선을 관리한다")
    @Test
    void manageLine() {
        // when
        createLine("신분당선", "10");
        createLine("1호선", "11");
        createLine("2호선", "12");
        createLine("3호선", "13");
        // then
        List<LineServiceResponse> lines = getLines();
        assertThat(lines.size()).isEqualTo(4);

        // when
        LineServiceResponse line = getLine(lines.get(0).getId());
        // then
        assertThat(line.getId()).isNotNull();
        assertThat(line.getName()).isNotNull();
        assertThat(line.getStartTime()).isNotNull();
        assertThat(line.getEndTime()).isNotNull();
        assertThat(line.getIntervalTime()).isNotNull();
        assertThat(line.getLineColor()).isNotNull();

        // when
        LocalTime startTime = LocalTime.of(8, 00);
        LocalTime endTime = LocalTime.of(22, 00);
        updateLine(line, startTime, endTime);
        //then
        LineServiceResponse updatedLine = getLine(line.getId());
        assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
        assertThat(updatedLine.getEndTime()).isEqualTo(endTime);

        // when
        deleteLine(line.getId());
        // then
        List<LineServiceResponse> linesAfterDelete = getLines();
        assertThat(linesAfterDelete.size()).isEqualTo(3);
    }

    private LineServiceResponse getLine(Long id) {
        return given().when().
                        get("/lines/" + id).
                then().
                        log().all().
                        extract().as(LineServiceResponse.class);
    }

    private void createLine(String name, String intervalTime) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", intervalTime);
        params.put("lineColor", "bg-pink-700" + name);

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

    private void updateLine(LineServiceResponse line, LocalTime startTime, LocalTime endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("name", line.getName());
        params.put("startTime", LocalTime.of(8, 00).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(22, 00).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("lineColor", line.getLineColor());

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                put("/lines/" + line.getId()).
        then().
                log().all().
                statusCode(HttpStatus.NO_CONTENT.value());
    }

    private List<LineServiceResponse> getLines() {
        return  given().
                when().
                    get("/lines").
                then().
                    log().all().
                    extract().
                    jsonPath().getList(".", LineServiceResponse.class);

    }

    private void deleteLine(Long id) {
        given().
                when().
                delete("/lines/" + id).
                then().
                log().all();
    }
    // @formatter:on

}
