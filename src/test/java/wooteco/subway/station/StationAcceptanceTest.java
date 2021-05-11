package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.TestUtils;
import wooteco.subway.station.controller.dto.StationRequest;
import wooteco.subway.station.controller.dto.StationResponse;


@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        final StationRequest stationRequest = new StationRequest("수원역");

        // when
        final ExtractableResponse<Response> response = TestUtils.postStation(stationRequest);

        // then
        final StationResponse stationResponse = response.body().as(StationResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationResponse.getName()).isEqualTo(stationRequest.getName());
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final StationRequest stationRequest = new StationRequest("수원역");
        TestUtils.postStation(stationRequest);

        // when
        final ExtractableResponse<Response> response = TestUtils.postStation(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void showStations() {
        /// given
        final StationRequest stationRequest1 = new StationRequest("수원역");
        final ExtractableResponse<Response> createResponse1 = TestUtils.postStation(stationRequest1);

        final StationRequest stationRequest2 = new StationRequest("역삼역");
        final ExtractableResponse<Response> createResponse2 = TestUtils.postStation(stationRequest2);

        // when
        final ExtractableResponse<Response> response = TestUtils.getStations();

        // then
        final List<Long> resultStationIds = resultStationsIds(response);
        final List<Long> expectedStationIds = Arrays.asList(stationId(createResponse1), stationId(createResponse2));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    private Long stationId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    private List<Long> resultStationsIds(final ExtractableResponse<Response> response) {
        final JsonPath jsonPath = response.jsonPath();
        final List<StationResponse> stationResponses = jsonPath.getList(".", StationResponse.class);

        return stationResponses.stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final StationRequest stationRequest = new StationRequest("수원역");
        final ExtractableResponse<Response> createResponse = TestUtils.postStation(stationRequest);

        // when
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = TestUtils.deleteStation(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
