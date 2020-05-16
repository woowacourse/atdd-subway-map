package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.*;
import static wooteco.subway.admin.acceptance.LineAcceptanceTest.*;
import static wooteco.subway.admin.acceptance.StationAcceptanceTest.*;

import java.util.List;

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
import wooteco.subway.admin.dto.request.LineStationCreateRequest;
import wooteco.subway.admin.dto.resopnse.LineDetailResponse;
import wooteco.subway.admin.dto.resopnse.StationResponse;

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

    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // given 지하철 역이 여러 개 추가되어있다.
        Long jamsilId = createStation("잠실역");
        Long jamsilSaenaeId = createStation("잠실새내역");
        Long seoknamId = createStation("석남역");
        Long sindorimId = createStation("신도림역");
        Long bupeyongId = createStation("부평역");
        // and 지하철 노선이 추가되어있다.
        Long lineId = createLine("2호선");

        // when 지하철 노선에 지하철역을 등록하는 요청을 한다.
        // then 지하철역이 노선에 추가 되었다.
        register(lineId, null, jamsilId);
        register(lineId, jamsilId, jamsilSaenaeId);
        register(lineId, jamsilSaenaeId, seoknamId);
        register(lineId, seoknamId, sindorimId);
        register(lineId, sindorimId, bupeyongId);

        // when 지하철 노선의 지하철역 목록 조회 요청을 한다.
        // then 지하철역 목록을 응답 받는다.
        LineDetailResponse line = getLineDetail(lineId);
        List<StationResponse> stations = line.getStations();
        // and 새로 추가한 지하철역을 목록에서 찾는다.
        StationResponse jamsil = getStation(jamsilId);
        assertThat(stations).contains(jamsil);
        assertThat(stations).contains(getStation(jamsilSaenaeId));
        assertThat(stations).contains(getStation(seoknamId));
        assertThat(stations).contains(getStation(sindorimId));
        assertThat(stations).contains(getStation(bupeyongId));

        // when 지하철 노선에 포함된 특정 지하철역을 제외하는 요청을 한다.
        // then 지하철역이 노선에서 제거 되었다.
        deleteStationOnLine(lineId, jamsilId);

        // when 지하철 노선의 지하철역 목록 조회 요청을 한다.
        // then 지하철역 목록을 응답 받는다.
        LineDetailResponse deletedLineResponse = getLineDetail(lineId);
        List<StationResponse> deletedStations = deletedLineResponse.getStations();
        // and 제외한 지하철역이 목록에 존재하지 않는다.
        assertThat(deletedStations).doesNotContain(jamsil);
    }

    private LineDetailResponse getLineDetail(Long lineId) {
        return given().when().
            get("/lines/" + lineId + "/detail").
            then().
            log().all().
            extract().as(LineDetailResponse.class);
    }

    private void register(Long lineId, Long preStationId, Long stationId) {
        LineStationCreateRequest lineStationCreateRequest =
            new LineStationCreateRequest(preStationId, stationId, 10, 10);

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

    private void deleteStationOnLine(Long lineId, Long stationId) {
        given().when()
            .delete("/lines/" + lineId + "/stations/" + stationId)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
