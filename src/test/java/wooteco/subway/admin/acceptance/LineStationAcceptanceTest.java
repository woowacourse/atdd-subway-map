package wooteco.subway.admin.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class LineStationAcceptanceTest extends AcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
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
        // Given
        createStation("잠실역");
        createStation("종합운동장역");
        createStation("선릉역");
        createStation("강남역");
        // And
        createLine("신분당선");
        createLine("1호선");
        createLine("2호선");
        createLine("3호선");

        // When
        createLineStation(3L, null, 1L, 10, 10);
        createLineStation(3L, 1L, 2L, 10, 10);
        createLineStation(3L, 2L, 3L, 10, 10);
        createLineStation(3L, 3L, 4L, 10, 10);
        // Then
        LineResponse lineResponse = getLineWithStations(3L);
        assertThat(lineResponse.getStations().size()).isEqualTo(4);
        // And
        assertThat(lineResponse.getStations()
                .stream()
                .map(StationResponse::getName)).contains("잠실역", "종합운동장역", "선릉역", "강남역");

        // When
        deleteLineStation(3L, 1L);
        // Then
        assertThat(getLineWithStations(3L).getStations().size()).isEqualTo(3);

        // When Then
        List<StationResponse> stations2 = getLineWithStations(3L).getStations();
        // And
        assertThat(stations2
                .stream()
                .map(StationResponse::getName)).doesNotContain("잠실역");
    }

    private void createLineStation(Long lineId, Long preStationId, Long stationId, int distance, int duration) {
        LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(preStationId, stationId, distance, duration);

        given().
                body(lineStationCreateRequest).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                accept(MediaType.APPLICATION_JSON_VALUE).
        when().
                post("/lines/" + lineId + "/stations").
        then().
                log().all().
                statusCode(HttpStatus.CREATED.value());
    }

    private LineResponse getLineWithStations(Long lineId) {
        return given().
                when().
                    get("/lines/" + lineId + "/stations").
                then().
                    log().all().
                    statusCode(HttpStatus.OK.value()).
                    extract().as(LineResponse.class);
    }

    private void deleteLineStation(Long lineId, Long stationId) {
        given().
        when().
                delete("/lines/" + lineId + "/stations/" + stationId).
        then().
                log().all().
                statusCode(HttpStatus.NO_CONTENT.value());
    }
}
