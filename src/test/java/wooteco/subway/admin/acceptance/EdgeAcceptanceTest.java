package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.dto.EdgeCreateRequest;
import wooteco.subway.admin.dto.EdgeDeleteRequest;
import wooteco.subway.admin.dto.EdgeResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class EdgeAcceptanceTest {

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
        createStation("선릉역");
        createStation("강남역");

        createLine("2호선");

        Long lineId = 1L;
        Long preStationId = 1L;
        Long stationId = 2L;
        EdgeCreateRequest edgeCreateRequest = new EdgeCreateRequest(preStationId, stationId, 10, 10);

        Long edgeId = 1L;

        given()
                .body(edgeCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/edge", lineId)
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value());

        JsonPath edgeJsonPathByLineId = given()
                .when()
                .get("/lines/" + lineId + "/edge")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath();

        assertThat((Object) edgeJsonPathByLineId.get("[0].preStationId")).isEqualTo(1);
        assertThat((Object) edgeJsonPathByLineId.get("[0].stationId")).isEqualTo(2);


        EdgeDeleteRequest edgeDeleteRequest = new EdgeDeleteRequest(preStationId, stationId);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(edgeDeleteRequest)
                .when()
                .delete("/lines/" + lineId + "/edge")
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());

        List<EdgeResponse> edgeResponses = given()
                .when()
                .get("/lines/" + lineId + "/edge")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList(".", EdgeResponse.class);

        assertThat(edgeResponses).isEmpty();
    }

    private void createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");
        params.put("bgColor", "bg-red-200");

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

}
