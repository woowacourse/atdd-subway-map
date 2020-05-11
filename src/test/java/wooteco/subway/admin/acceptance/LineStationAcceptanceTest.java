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
import wooteco.subway.admin.dto.LineResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    /**
     * Given 지하철역이 여러 개 추가되어있다.
     * And 지하철 노선이 추가되어있다.
     * <p>
     * When 지하철 노선에 지하철역을 등록하는 요청을 한다.
     * Then 지하철역이 노선에 추가 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 새로 추가한 지하철역을 목록에서 찾는다.
     * <p>
     * When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
     * Then 지하철역이 노선에서 제거 되었다.
     * <p>
     * When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     * Then 지하철역 목록을 응답 받는다.
     * And 제외한 지하철역이 목록에 존재하지 않는다.
     */
    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // given - and
        createStation("잠실역");
        createStation("잠실새내역");
        createStation("종합운동장역");
        LineResponse line = createLine("2호선");
        // when
        registerStation(line.getId());
        // then
        getLine(line.getId());
        //when & then
        getStationsOfLine(line.getId());
        //when & then
        deleteStationOfLine(line.getId());
        //when & then
        getStationsOfLineButCanNotFind(line.getId());
    }

    private void getStationsOfLineButCanNotFind(long lineId) {
        given()
                .when().
                get("/lines/" + lineId)
                .then()
                .log().all().
                statusCode(HttpStatus.NOT_FOUND.value());
    }

    private void deleteStationOfLine(long lineId) {
        given()
                .when().
                delete("/lines/" + lineId).
                then().
                log().all().
                statusCode(HttpStatus.NO_CONTENT.value());

    }

    private void getStationsOfLine(long lineId) {
        given()
                .when().
                get("/lines/" + lineId + "/stations").
                then().
                log().all().
                statusCode(HttpStatus.OK.value());

    }

    private void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                post("/stations").
        then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private LineResponse createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor","red");

        return given().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                post("/lines").
        then().
                log().all().
                statusCode(HttpStatus.CREATED.value())
                .extract().as(LineResponse.class);
    }

    private void registerStation(Long id) {

        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("preStationName", "종합운동장역");
        params.put("distance", "10");
        params.put("duraction", "10");
        given().
                body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE).

        when().
                post("/lines/" + id + "/register").
        then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private void getLine(Long id) {
        given()
                .when().
                get("/lines/" + id).
                then().
                log().all().
                statusCode(HttpStatus.OK.value());
    }
}
