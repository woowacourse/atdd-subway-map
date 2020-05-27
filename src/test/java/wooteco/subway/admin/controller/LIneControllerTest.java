package wooteco.subway.admin.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LIneControllerTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    @DisplayName("잘못된 요청(title)으로 Line 을 create할때")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void notValidateTitle(String title) {
        Map<String, String> params = getParamsByTitle(title);
        createLine(params);
    }

    @DisplayName("잘못된 요청(시간)으로 Line 을 create할때")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void notValidateTime(String time) {
        Map<String, String> params = getParamsByTime(time);
        createLine(params);
    }

    private void createLine(Map<String, String> params) {
        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines").
            then().
            log().all().
            statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> getParamsByTitle(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("title", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("backgroundColor", "white");
        return params;
    }

    private Map<String, String> getParamsByTime(String time) {
        Map<String, String> params = new HashMap<>();
        params.put("title", "1호선");
        params.put("startTime", time);
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("backgroundColor", "white");
        return params;
    }
}
