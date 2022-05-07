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
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionSaveRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

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

    @Test
    @DisplayName("구간을 추가한다.")
    void saveSection() {
        // given
        Long stationId1 = postStations(new StationRequest("강남역"))
                .as(StationResponse.class)
                .getId();
        Long stationId2 = postStations(new StationRequest("역삼역"))
                .as(StationResponse.class)
                .getId();
        Long stationId3 = postStations(new StationRequest("선릉역"))
                .as(StationResponse.class)
                .getId();
        Long lineId = postLines(new LineRequest("신분당선", "bg-red-600", stationId1, stationId3, 10))
                .as(LineResponse.class)
                .getId();
        SectionSaveRequest sectionSaveRequest = new SectionSaveRequest(stationId1, stationId2, 3);

        // when
        ExtractableResponse<Response> response = postSections(lineId, sectionSaveRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
