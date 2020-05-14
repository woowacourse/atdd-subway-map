package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import wooteco.subway.admin.dto.LineResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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

    @DisplayName("노선을 관리한다.")
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromCollection() {
        return Stream.of(
            dynamicTest("노선을 만드는 요청을 보내 추가한다.", () -> {
                // when
                createLine("신분당선");
                createLine("1호선");
                createLine("2호선");
                createLine("3호선");
                createLine("3호선");

                // then
                List<LineResponse> lines = getLines();
                assertThat(lines.size()).isEqualTo(4);
            }),
            dynamicTest("노선에 접근한다.", () -> {
                // given
                List<LineResponse> lines = getLines();

                // when
                LineResponse line = lines.get(0);

                // then
                assertThat(line.getId()).isNotNull();
                assertThat(line.getName()).isNotNull();
                assertThat(line.getStartTime()).isNotNull();
                assertThat(line.getEndTime()).isNotNull();
                assertThat(line.getIntervalTime()).isNotNull();
            }),
            dynamicTest("노선을 업데이트한다.", () -> {
                // given
                List<LineResponse> lines = getLines();
                LineResponse line = lines.get(0);

                // when
                LocalTime startTime = LocalTime.of(8, 00);
                LocalTime endTime = LocalTime.of(22, 00);
                updateLine(line.getId(), startTime, endTime);

                // then
                LineResponse updatedLine = getLine(line.getId());
                assertThat(updatedLine.getStartTime()).isEqualTo(startTime);
                assertThat(updatedLine.getEndTime()).isEqualTo(endTime);
            }),
            dynamicTest("노선을 삭제한다.", () -> {
                // given
                List<LineResponse> lines = getLines();
                LineResponse line = lines.get(0);

                // when
                deleteLine(line.getId());

                // then
                List<LineResponse> linesAfterDelete = getLines();
                assertThat(linesAfterDelete.size()).isEqualTo(3);
            })
        );
    }

    private LineResponse getLine(Long id) {
        // @formatter:off
        return given()
        .when()
            .get("/lines/" + id)
        .then()
            .log().all()
            .extract().as(LineResponse.class);
        // @formatter:on
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor", "bg-gray-100");
        // @formatter:off
        given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post("/lines")
        .then()
            .log().all()
            .statusCode(anyOf(
                is(HttpStatus.CREATED.value()),
                is(HttpStatus.BAD_REQUEST.value()))
            );
        // @formatter:on
    }

    private void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        // @formatter:off
        given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .put("/lines/" + id)
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value());
        // @formatter:on
    }

    private List<LineResponse> getLines() {
        // @formatter:off
        return given().
            when().
                get("/lines").
            then().
                log().all().
                extract().
                jsonPath().getList(".", LineResponse.class);
        // @formatter:on
    }

    private void deleteLine(Long id) {
        // @formatter:off
        given()
        .when()
            .delete("/lines/" + id)
        .then()
            .log().all();
        // @formatter:on
    }
}
