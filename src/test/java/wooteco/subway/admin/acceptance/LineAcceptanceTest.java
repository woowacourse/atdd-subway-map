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

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.controller.advice.ApiError;
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
        // when 지하철 노선 n개 추가 요청을 한다.
        createLine("신분당선");
        createLine("1호선");
        createLine("2호선");
        createLine("3호선");
        ApiError error = createLineException("1호선");
        assertThat(error.getMessage()).contains("이미 존재하는 이름");
        // when 지하철 노선 목록 조회 요청을 한다.
        // then 지하철 노선 목록을 응답 받는다.
        List<LineResponse> lines = getLines();
        // and 지하철 노선 목록은 4개이다.
        assertThat(lines.size()).isEqualTo(4);

        // when
        LineResponse line = getLine(lines.get(0).getId());
        // then
        assertThat(line.getId()).isNotNull();
        assertThat(line.getName()).isNotNull();
        assertThat(line.getStartTime()).isNotNull();
        assertThat(line.getEndTime()).isNotNull();
        assertThat(line.getIntervalTime()).isNotNull();

        // when 지하철 노선 수정 요청을 한다.
        LocalTime startTime = LocalTime.of(8, 00);
        LocalTime endTime = LocalTime.of(22, 00);
        updateLine(line.getId(), startTime, endTime);
        //then 지하철 노선이 수정 되었다.
        LineResponse updatedLine = getLine(line.getId());
        assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
        assertThat(updatedLine.getEndTime()).isEqualTo(endTime);

        // when 지하철 노선 제거 요청을 한다.
        // then 지하철 노선이 제거 되었다.
        deleteLine(line.getId());
        // when 지하철 노선 목록 조회를 요청을 한다.
        // then 지하철 노선 목록을 응답 받는다.
        List<LineResponse> linesAfterDelete = getLines();
        // and 지하철 노선 목록은 3개이다.
        assertThat(linesAfterDelete.size()).isEqualTo(3);
    }

    private ApiError createLineException(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-green-700");
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

        return given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines").
            then().
            log().all().
            statusCode(HttpStatus.BAD_REQUEST.value()).
            extract().
            as(ApiError.class);
    }

    static LineResponse getLine(Long id) {
        return given().when().
            get("/lines/" + id).
            then().
            log().all().
            extract().as(LineResponse.class);
    }

    static Long createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "bg-green-700");
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

        return given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value()).
            extract().
            body().
            as(Long.class);
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
            put("/lines/" + id).
            then().
            log().all().
            statusCode(HttpStatus.OK.value());
    }

    private List<LineResponse> getLines() {
        return
            given().
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
            log().all();
    }
}
