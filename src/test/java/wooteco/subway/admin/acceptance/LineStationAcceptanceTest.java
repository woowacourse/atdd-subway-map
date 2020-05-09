package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;

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
import wooteco.subway.admin.dto.StationResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/truncate.sql", "/insert.sql"})
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
     *     When 지하철 노선의 지하철역 목록 조회 요청을 한다.
     *     Then 지하철역 목록을 응답 받는다.
     *     And 제외한 지하철역이 목록에 존재하지 않는다.
     */

    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // Given 지하철역이 여러 개 추가되어있다.
        // And 지하철 노선이 추가되어있다.

        // When 지하철 노선에 지하철역을 등록하는 요청을 한다.
        // Then 지하철역이 노선에 추가 되었다.
        appendStationToLine();

        // When 지하철 노선의 지하철역 목록 조회 요청을 한다.
        // Then 지하철역 목록을 응답 받는다.
        List<StationResponse> responses = getStations();
        assertThat(responses.size()).isEqualTo(5);
        // And 새로 추가한 지하철역을 목록에서 찾는다.
        assertThat(responses.get(4).getName()).isEqualTo("석촌역");

        // When 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
        // Then 지하철역이 노선에서 제거 되었다.
        excludeStationFromLine();
    }

    private void appendStationToLine() {
        Map<String, String> params = new HashMap<>();
        params.put("id", "5");
        params.put("name", "석촌역");

        given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines/" + 1 + "/stations").
            then().
            log().all().
            statusCode(HttpStatus.CREATED.value());
    }

    private List<StationResponse> getStations() {
        return given().
            when().
            get("/lines/1/stations").
            then().
            log().all().
            extract().
            jsonPath().getList(".", StationResponse.class);
    }

    private void excludeStationFromLine() {
        given().
            when().
            delete("/lines/" + 1 + "/stations/" + 5).
            then().
            log().all().
            statusCode(HttpStatus.NO_CONTENT.value());
    }
}
