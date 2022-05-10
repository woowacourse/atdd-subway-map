package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_강남역;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_역삼역;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_잠실역;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.request.StationRequest;

public class StationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // given & when
        ExtractableResponse<Response> response = AcceptanceTestUtil.requestPostStation(STATION_REQUEST_강남역,
            "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("중복된 이름으로 지하철역을 생성하면 예외를 발생시킨다.")
    void createStationWithDuplicateName() {
        // given
        AcceptanceTestUtil.requestPostStation(STATION_REQUEST_강남역, "/stations");

        // when, then
        final ExtractableResponse<Response> response = AcceptanceTestUtil.requestPostStation(STATION_REQUEST_강남역,
            "/stations");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = AcceptanceTestUtil.requestPostStation(STATION_REQUEST_잠실역,
            "/stations");

        ExtractableResponse<Response> createResponse2 = AcceptanceTestUtil.requestPostStation(STATION_REQUEST_역삼역,
            "/stations");

        // when
        ExtractableResponse<Response> response = AcceptanceTestUtil.requestGetStations("/stations");
        List<Long> expectedStationIds = AcceptanceTestUtil.getExpectedStationIds(createResponse1, createResponse2);
        List<Long> resultStationIds = AcceptanceTestUtil.getResultStationIds(response);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(resultStationIds).containsAll(expectedStationIds)
        );
    }

    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        StationRequest request = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = AcceptanceTestUtil.requestPostStation(request, "/stations");

        // when
        final ExtractableResponse<Response> response = AcceptanceTestUtil.requestDeleteStation(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
