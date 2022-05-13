package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

@DisplayName("구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("구간을 생성한다.")
    void createSection() {
        // given
        Long savedStationId1 = saveStation(new StationRequest("지하철역이름"));
        Long savedStationId2 = saveStation(new StationRequest("새로운지하철역이름"));
        Long savedStationId3 = saveStation(new StationRequest("또다른지하철역이름"));
        Long savedLineId = saveLine(savedStationId1, savedStationId2);
        SectionRequest request = new SectionRequest(savedStationId1, savedStationId3, 50);

        // when
        ExtractableResponse<Response> response = requestSectionCreation(savedLineId, request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private Long saveStation(StationRequest request) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        return Long.valueOf(extractLocationFromHeader(response).split("/")[2]);
    }

    private Long saveLine(Long stationId1, Long stationId2) {
        LineRequest request = new LineRequest("신분당선", "bg-red-600", stationId1, stationId2, 100);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        return Long.valueOf(extractLocationFromHeader(response).split("/")[2]);
    }

    private ExtractableResponse<Response> requestSectionCreation(Long savedLineId, SectionRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + savedLineId + "/sections")
                .then()
                .log().all()
                .extract();
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() {
        // given
        Long savedStationId1 = saveStation(new StationRequest("지하철역이름"));
        Long savedStationId2 = saveStation(new StationRequest("새로운지하철역이름"));
        Long savedStationId3 = saveStation(new StationRequest("또다른지하철역이름"));
        Long savedLineId = saveLine(savedStationId1, savedStationId2);
        SectionRequest request = new SectionRequest(savedStationId3, savedStationId1, 50);
        requestSectionCreation(savedLineId, request);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .queryParam("stationId", savedStationId2)
                .when()
                .delete("/lines/" + savedLineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
