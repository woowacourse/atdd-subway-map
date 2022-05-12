package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("환승역(교점), 두 노선이 같은 역을 공유하는 경우에도 구간을 등록할 수 있어야 한다.")
    @Test
    public void crossSectionTest() {
        // given
        final Long stationId1 = extractStationIdFromName("교대역");
        final Long stationId2 = extractStationIdFromName("강남역");
        final Long stationId3 = extractStationIdFromName("역삼역");

        final LineRequest params1 = new LineRequest("2호선", "bg-red-600", stationId1, stationId2, 10);
        ExtractableResponse<Response> response1 = AcceptanceFixture.post(params1, "/lines");
        Long lineId1 = extractId(response1);

        SectionRequest sectionRequest1 = new SectionRequest(stationId2, stationId3, 5);
        ExtractableResponse<Response> sectionResponse1 = AcceptanceFixture.post(sectionRequest1, "/lines/" + lineId1 + "/sections");

        final Long stationId4 = extractStationIdFromName("신사역");
        final Long stationId5 = extractStationIdFromName("양재역");

        final LineRequest params2 = new LineRequest("신분당선", "bg-red-600", stationId4, stationId2, 10);
        ExtractableResponse<Response> response2 = AcceptanceFixture.post(params2, "/lines");
        Long lineId2 = extractId(response2);

        SectionRequest sectionRequest2 = new SectionRequest(stationId2, stationId5, 5);
        ExtractableResponse<Response> sectionResponse2 = AcceptanceFixture.post(sectionRequest2, "/lines/" + lineId2 + "/sections");

        //when & then
        assertThat(sectionResponse1.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(sectionResponse2.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
    
    @DisplayName("갈래길을 방지하여 구간을 등록한다.")
    @Test
    public void addPreventFork() {
        // given
        final Long stationId1 = extractStationIdFromName("교대역");
        final Long stationId2 = extractStationIdFromName("강남역");
        final Long stationId3 = extractStationIdFromName("역삼역");

        final LineRequest params = new LineRequest("2호선", "bg-red-600", stationId1, stationId3, 7);
        ExtractableResponse<Response> response = AcceptanceFixture.post(params, "/lines");
        Long lineId = extractId(response);

        final SectionRequest sectionRequest = new SectionRequest(stationId1, stationId2, 4);

        // when
        AcceptanceFixture.post(sectionRequest, "/lines/" + lineId + "/sections");

        // then
        final ExtractableResponse<Response> result = AcceptanceFixture.get("/lines/" + lineId);
        assertThat(result.as(LineResponse.class).getName()).isEqualTo("2호선");
        assertThat(result.as(LineResponse.class).getColor()).isEqualTo("bg-red-600");
        assertThat(result.as(LineResponse.class).getStations()).hasSize(3)
                .extracting("name")
                .containsExactly("교대역", "강남역", "역삼역");
    }

    @DisplayName("구간을 제거할 수 있다.")
    @Test
    public void deleteSection() {
        // given
        final Long stationId1 = extractStationIdFromName("교대역");
        final Long stationId2 = extractStationIdFromName("강남역");
        final Long stationId3 = extractStationIdFromName("역삼역");

        final LineRequest params = new LineRequest("2호선", "bg-red-600", stationId1, stationId3, 7);
        ExtractableResponse<Response> response = AcceptanceFixture.post(params, "/lines");
        Long lineId = extractId(response);

        final SectionRequest sectionRequest = new SectionRequest(stationId1, stationId2, 4);

        AcceptanceFixture.post(sectionRequest, "/lines/" + lineId + "/sections");

        // when
        AcceptanceFixture.delete("/lines/" + lineId + "/sections?stationId=" + stationId2);

        // then
        final ExtractableResponse<Response> result = AcceptanceFixture.get("/lines/" + lineId);
        assertThat(result.as(LineResponse.class).getName()).isEqualTo("2호선");
        assertThat(result.as(LineResponse.class).getColor()).isEqualTo("bg-red-600");
        assertThat(result.as(LineResponse.class).getStations()).hasSize(2)
                .extracting("name")
                .containsExactly("교대역", "역삼역");
    }

    private Long extractId(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", LineResponse.class)
                .getId();
    }

    private Long extractStationIdFromName(String name) {
        final StationRequest stationRequest = new StationRequest(name);
        final ExtractableResponse<Response> stationResponse = AcceptanceFixture.post(stationRequest, "/stations");

        return stationResponse.jsonPath()
                .getObject(".", StationResponse.class)
                .getId();
    }
}
