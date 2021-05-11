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
        final StationRequest jamsilRequest = TestUtils.JAMSIL_STATION_REQUEST;

        // when
        final ExtractableResponse<Response> jamsilResponse = TestUtils.postStation(jamsilRequest);

        // then
        final StationResponse stationResponse = jamsilResponse.body().as(StationResponse.class);
        assertThat(jamsilResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(stationResponse.getName()).isEqualTo(jamsilRequest.getName());
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final StationRequest jamsilRequest = TestUtils.JAMSIL_STATION_REQUEST;
        TestUtils.postStation(jamsilRequest);

        // when
        final ExtractableResponse<Response> jamsilSecondResponse = TestUtils.postStation(jamsilRequest);

        // then
        assertThat(jamsilSecondResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void showStations() {
        /// given
        final StationRequest jamsilRequest = TestUtils.JAMSIL_STATION_REQUEST;
        final ExtractableResponse<Response> jamsilResponse = TestUtils.postStation(jamsilRequest);

        final StationRequest gangnamRequest = TestUtils.GANGNAM_STATION_REQUEST;
        final ExtractableResponse<Response> gangnamResponse = TestUtils.postStation(gangnamRequest);

        // when
        final ExtractableResponse<Response> getStationsResponse = TestUtils.getStations();

        // then
        final List<Long> resultStationIds = resultStationsIds(getStationsResponse);
        final List<Long> expectedStationIds = Arrays.asList(stationId(jamsilResponse), stationId(gangnamResponse));

        assertThat(getStationsResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
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
        final StationRequest jamsilRequest = TestUtils.JAMSIL_STATION_REQUEST;
        final ExtractableResponse<Response> jamsilResponse = TestUtils.postStation(jamsilRequest);

        // when
        final String uri = jamsilResponse.header("Location");
        final ExtractableResponse<Response> deleteResponse = TestUtils.deleteStation(uri);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
