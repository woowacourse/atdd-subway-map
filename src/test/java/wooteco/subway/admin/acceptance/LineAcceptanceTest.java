package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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

    @TestFactory
    public Stream<DynamicTest> lineTest() {
        return Stream.of(
            DynamicTest.dynamicTest("create line test", () -> {
                //given
                createLine("신분당선");
                createLine("1호선");
                createLine("2호선");
                createLine("3호선");

                //when
                List<LineResponse> lines = getLines();

                // then
                assertThat(lines.size()).isEqualTo(4);
            }),
            DynamicTest.dynamicTest("check line data in DB test", () -> {
                //given
                List<LineResponse> lines = getLines();

                // when
                LineResponse line = getLine(lines.get(0).getId());

                // then
                assertThat(line.getId()).isNotNull();
                assertThat(line.getName()).isNotNull();
                assertThat(line.getStartTime()).isNotNull();
                assertThat(line.getEndTime()).isNotNull();
                assertThat(line.getIntervalTime()).isNotNull();
            }),
            DynamicTest.dynamicTest("updata line test", () -> {
                //given
                LocalTime startTime = LocalTime.of(8, 00);
                LocalTime endTime = LocalTime.of(22, 00);
                List<LineResponse> lines = getLines();
                LineResponse line = getLine(lines.get(0).getId());

                //when
                updateLine(line.getId(), startTime, endTime);

                //then
                LineResponse updatedLine = getLine(line.getId());
                assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
                assertThat(updatedLine.getEndTime()).isEqualTo(endTime);
            }),
            DynamicTest.dynamicTest("delete line test", () -> {
                //given
                List<LineResponse> lines = getLines();
                LineResponse line = getLine(lines.get(0).getId());

                // when
                deleteLine(line.getId());

                // then
                List<LineResponse> linesAfterDelete = getLines();
                assertThat(linesAfterDelete.size()).isEqualTo(3);
            })
        );
    }

    private LineResponse getLine(Long id) {
        return given().when().
            get("/api/lines/" + id).
            then().
            log().all().
            extract().as(LineResponse.class);
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("bgColor", "red");
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

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
                statusCode(HttpStatus.OK.value());
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
