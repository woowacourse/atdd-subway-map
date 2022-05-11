package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class SectionAcceptanceTest extends AcceptanceTest {

    private Long createdLineId;
    private Long createdStationId1;
    private Long createdStationId2;
    private Long createdStationId3;
    private Long createdStationId4;
    private Long createdStationId5;

    @BeforeEach
    void createLineAndStations() {
        createdStationId1 = AcceptanceUtil.createStation("선릉역");
        createdStationId2 = AcceptanceUtil.createStation("삼성역");
        createdStationId3 = AcceptanceUtil.createStation("종합운동장역");
        createdStationId4 = AcceptanceUtil.createStation("잠실새내역");
        createdStationId5 = AcceptanceUtil.createStation("잠실역");
        createdLineId = AcceptanceUtil.createLine("2호선", "bg-red-600", createdStationId2, createdStationId4,
                10);
    }

    @DisplayName("노선에 상행선 방향 구간을 추가한다")
    @Test
    void createSection_upStation() {
        // given
        long lineId = createdLineId;
        long upStationId = createdStationId1;
        long downStationId = createdStationId2;
        int distance = 10;

        // when
        ExtractableResponse<Response> response = requestCreateSection(lineId, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @DisplayName("노선에 하행선 방향 구간을 추가한다")
    @Test
    void createSection_downStation() {
        // given
        long lineId = createdLineId;
        long upStationId = createdStationId4;
        long downStationId = createdStationId5;
        int distance = 10;

        // when
        ExtractableResponse<Response> response = requestCreateSection(lineId, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @DisplayName("노선 중간에 구간을 삽입한다")
    @Test
    void createSection_inserting() {
        // given
        long lineId = createdLineId;
        long upStationId = createdStationId2;
        long downStationId = createdStationId3;
        int distance = 5;

        // when
        ExtractableResponse<Response> response = requestCreateSection(lineId, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @DisplayName("기존 구간보다 길이가 긴 구간을 삽입하면 BAD REQUEST를 반환한다")
    @Test
    void createSection_returnsBadRequestOnInsertingIfSectionIsLongerThanBase() {
        // given
        long lineId = createdLineId;
        long upStationId = createdStationId2;
        long downStationId = createdStationId3;
        int distance = 15;

        // when
        ExtractableResponse<Response> response = requestCreateSection(lineId, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    @DisplayName("추가하려는 구간의 모든 역이 이미 노선에 모두 존재할 경우 예외가 발생한다")
    @Test
    void createSection_returnBadRequestIfBothUpAndDownStationAreAlreadyExisting() {
        // given
        long lineId = createdLineId;
        long upStationId = createdStationId2;
        long downStationId = createdStationId4;
        int distance = 10;

        // when
        ExtractableResponse<Response> response = requestCreateSection(lineId, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    @DisplayName("추가하려는 구간의 모든 역이 이미 노선에 모두 없는 경우 예외가 발생한다")
    @Test
    void createSection_returnBadRequestIfBothUpAndDownStationAreNotExisting() {
        // given
        long lineId = createdLineId;
        long upStationId = createdStationId1;
        long downStationId = createdStationId5;
        int distance = 10;

        // when
        ExtractableResponse<Response> response = requestCreateSection(lineId, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    @DisplayName("노선의 상행종점역 제거")
    @Test
    void deleteSection_upStation() {
        // given
        long lineId = createdLineId;
        long stationId = createdStationId2;

        requestCreateSection(lineId, createdStationId4, createdStationId5, 10);

        // when
        ExtractableResponse<Response> response = requestDeleteStation(lineId, stationId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    @DisplayName("노선의 하행종점역 제거")
    @Test
    void deleteSection_downStation() {
        // given
        long lineId = createdLineId;
        long stationId = createdStationId4;

        requestCreateSection(lineId, createdStationId1, createdStationId2, 10);

        // when
        ExtractableResponse<Response> response = requestDeleteStation(lineId, stationId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    @DisplayName("노선의 중간역 제거")
    @Test
    void deleteSection_betweenStation() {
        // given
        long lineId = createdLineId;
        long stationId = createdStationId3;

        requestCreateSection(lineId, createdStationId2, createdStationId3, 5);

        // when
        ExtractableResponse<Response> response = requestDeleteStation(lineId, stationId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value())
        );
    }

    @DisplayName("구간이 단 하나인 노선에서 역을 제거하면 예외가 발생한다")
    @Test
    void deleteSection_returnBadRequestIfIfSectionsSizeIsOne() {
        // given
        long lineId = createdLineId;
        long stationId = createdStationId2;

        // when
        ExtractableResponse<Response> response = requestDeleteStation(lineId, stationId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    private ExtractableResponse<Response> requestCreateSection(Long lineId, Long upStationId, Long downStationId,
                                                               Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("lineId", lineId.toString());
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance.toString());

        return AcceptanceUtil.postRequest(params, "/lines/" + lineId + "/sections");
    }

    private ExtractableResponse<Response> requestDeleteStation(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }
}

