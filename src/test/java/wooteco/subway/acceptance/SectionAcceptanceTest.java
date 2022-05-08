package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.LineAcceptanceTest.postLines;
import static wooteco.subway.acceptance.StationAcceptanceTest.postStations;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.line.LineSaveRequest;
import wooteco.subway.dto.section.SectionSaveRequest;
import wooteco.subway.dto.station.StationResponse;
import wooteco.subway.dto.station.StationSaveRequest;

public class SectionAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> postSections(final Long lineId, final SectionSaveRequest sectionSaveRequest) {
        return RestAssured.given().log().all()
                .body(sectionSaveRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteSection(final Long lineId, final Long stationId) {
        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    @Test
    @DisplayName("구간을 추가한다.")
    void saveSection() {
        // given
        Long stationId1 = postStations(new StationSaveRequest("강남역"))
                .as(StationResponse.class)
                .getId();
        Long stationId2 = postStations(new StationSaveRequest("역삼역"))
                .as(StationResponse.class)
                .getId();
        Long stationId3 = postStations(new StationSaveRequest("선릉역"))
                .as(StationResponse.class)
                .getId();
        Long lineId = postLines(new LineSaveRequest("신분당선", "bg-red-600", stationId1, stationId3, 10))
                .as(LineResponse.class)
                .getId();
        SectionSaveRequest sectionSaveRequest = new SectionSaveRequest(stationId1, stationId2, 3);

        // when
        ExtractableResponse<Response> response = postSections(lineId, sectionSaveRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void removeSection() {
        // given
        Long stationId1 = postStations(new StationSaveRequest("강남역"))
                .as(StationResponse.class)
                .getId();
        Long stationId2 = postStations(new StationSaveRequest("역삼역"))
                .as(StationResponse.class)
                .getId();
        Long stationId3 = postStations(new StationSaveRequest("선릉역"))
                .as(StationResponse.class)
                .getId();

        Long lineId = postLines(new LineSaveRequest("신분당선", "bg-red-600", stationId1, stationId2, 10))
                .as(LineResponse.class)
                .getId();
        postSections(lineId, new SectionSaveRequest(stationId2, stationId3, 10));

        // when
        ExtractableResponse<Response> response = deleteSection(lineId, stationId2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
