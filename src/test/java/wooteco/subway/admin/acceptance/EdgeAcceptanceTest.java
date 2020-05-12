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
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.request.EdgeCreateRequest;
import wooteco.subway.admin.dto.response.LineResponse;
import wooteco.subway.admin.dto.response.StationsAtLineResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    void manageEdge() throws Exception {
        //given
        Station station1 = createStation("잠실역");
        Station station2 = createStation("삼성역");
        Station station3 = createStation("강변역");

        LineResponse lineResponse = createLine("2호선");

        //when 노선 추가
        EdgeCreateRequest edgeCreateRequest1 = new EdgeCreateRequest(null, "잠실역", 10, 10);
        EdgeCreateRequest edgeCreateRequest2 = new EdgeCreateRequest("잠실역", "삼성역", 10, 10);
        EdgeCreateRequest edgeCreateRequest3 = new EdgeCreateRequest("삼성역", "강변역", 10, 10);

        createEdge(lineResponse.getId(), edgeCreateRequest1);
        createEdge(lineResponse.getId(), edgeCreateRequest2);
        StationsAtLineResponse response = createEdge(lineResponse.getId(), edgeCreateRequest3);

        //then
        assertThat(response.getId()).isEqualTo(lineResponse.getId());
        assertThat(response.getStations().size()).isEqualTo(3);

        //when 노선의 지하철역 조회
        List<StationsAtLineResponse> allEdges = findAllEdges();
        //then
        List<Station> savedStations = allEdges.get(0).getStations();
        List<String> savedStationNames = savedStations.stream()
                .map(Station::getName)
                .collect(Collectors.toList());
        assertThat(savedStationNames).contains("잠실역");
        assertThat(savedStationNames).contains("삼성역");

        //when 노선의 특정 지하철역 제거
        deleteEdge(lineResponse.getId(), station3.getId());

        //then
        List<StationsAtLineResponse> deletedEdges = findAllEdges();
        List<Station> deletedStations = deletedEdges.get(0).getStations();
        assertThat(deletedStations.size()).isEqualTo(2);

    }

    private void deleteEdge(Long lineId, Long stationId) {
        given().when()
                .delete("/lines/" + lineId + "/edges/" + stationId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();
    }

    private List<StationsAtLineResponse> findAllEdges() {
        return given().
                when().
                get("/lines/edges").
                then().
                log().all().
                extract().
                jsonPath().getList(".", StationsAtLineResponse.class);
    }

    private Station createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return given().body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract()
                .as(Station.class);
    }

    private LineResponse createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("bgColor", "bg-green-500");
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
                extract().as(LineResponse.class);
    }

    private StationsAtLineResponse createEdge(Long lineId, EdgeCreateRequest request) throws NoSuchMethodException {
//        Constructor<StationsAtLineResponse> constructor = StationsAtLineResponse.class.getConstructor(null);
//        constructor.setAccessible(true);

        return given().body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/edges")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract()
                .as(StationsAtLineResponse.class);
    }
}
