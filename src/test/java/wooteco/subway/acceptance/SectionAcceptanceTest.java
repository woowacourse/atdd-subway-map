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

@DisplayName("지하철 노선 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private static final StationRequest 광흥창역 = new StationRequest("광흥창역");
    private static final StationRequest 상수역 = new StationRequest("상수역");

    private Long getPostLineId(LineRequest lineRequest) {
        return Long.valueOf(postLineResponse(lineRequest)
                .header("Location")
                .split("/")[2]);
    }

    private ExtractableResponse<Response> postSectionResponse(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteSectionResponse(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }

    private Long getLineId(Long stationId1, Long stationId2) {
        LineRequest 육호선 = new LineRequest("육호선", "bg-red-600", stationId1, stationId2, 10);
        return getPostLineId(육호선);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Long stationId1 = postStationId(대흥역);
        Long stationId2 = postStationId(공덕역);
        Long stationId3 = postStationId(광흥창역);
        Long lineId = getLineId(stationId1, stationId2);

        // when
        SectionRequest sectionRequest = new SectionRequest(stationId1, stationId3, 4);
        ExtractableResponse<Response> response = postSectionResponse(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("상행역과 하행역이 모두 노선에 존재하지 않은 지하철 구간을 생성한다.")
    @Test
    void createSectionBothIncludeInLine() {
        // given
        Long stationId1 = postStationId(대흥역);
        Long stationId2 = postStationId(공덕역);
        Long stationId3 = postStationId(광흥창역);
        Long stationId4 = postStationId(상수역);
        Long lineId = getLineId(stationId1, stationId2);

        // when
        SectionRequest sectionRequest = new SectionRequest(stationId3, stationId4, 4);
        ExtractableResponse<Response> response = postSectionResponse(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 모두 노선에 등록된 지하철 구간을 생성한다.")
    @Test
    void createSectionBothExcludeInLine() {
        // given
        Long stationId1 = postStationId(대흥역);
        Long stationId2 = postStationId(공덕역);
        Long lineId = getLineId(stationId1, stationId2);

        // when
        SectionRequest sectionRequest = new SectionRequest(stationId1, stationId2, 4);
        ExtractableResponse<Response> response = postSectionResponse(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간의 길이가 기존 역 사이의 길이보다 크거나 같은 지하철 구간을 생성한다.")
    @Test
    void createLongDistanceSection() {
        // given
        Long stationId1 = postStationId(대흥역);
        Long stationId2 = postStationId(공덕역);
        Long stationId3 = postStationId(광흥창역);
        Long lineId = getLineId(stationId1, stationId2);

        // when
        SectionRequest sectionRequest = new SectionRequest(stationId1, stationId3, 10);
        ExtractableResponse<Response> response = postSectionResponse(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        Long stationId1 = postStationId(대흥역);
        Long stationId2 = postStationId(공덕역);
        Long stationId3 = postStationId(광흥창역);
        Long lineId = getLineId(stationId1, stationId2);

        SectionRequest sectionRequest = new SectionRequest(stationId1, stationId3, 4);
        postSectionResponse(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> response = deleteSectionResponse(lineId, stationId2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간이 하나인 노선에서 마지막 지하철 구간을 삭제한다.")
    @Test
    void deleteLastSection() {
        // given
        Long stationId1 = postStationId(대흥역);
        Long stationId2 = postStationId(공덕역);
        Long lineId = getLineId(stationId1, stationId2);

        // when
        ExtractableResponse<Response> response = deleteSectionResponse(lineId, stationId2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
