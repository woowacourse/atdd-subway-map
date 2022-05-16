package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.AcceptanceTestUtil.getExpectedStationIds;
import static wooteco.subway.acceptance.AcceptanceTestUtil.getResultStationIds;
import static wooteco.subway.acceptance.AcceptanceTestUtil.requestDeleteStation;
import static wooteco.subway.acceptance.AcceptanceTestUtil.requestGetStations;
import static wooteco.subway.acceptance.AcceptanceTestUtil.requestPostStation;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_서울대역;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_신림역;
import static wooteco.subway.testutils.Fixture.STATION_REQUEST_역삼역;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class StationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // given & when
        ExtractableResponse<Response> response = requestPostStation(STATION_REQUEST_역삼역,
            "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("중복된 이름으로 지하철역을 생성하면 예외를 발생시킨다.")
    void createStationWithDuplicateName() {
        // given
        requestPostStation(STATION_REQUEST_신림역, "/stations");

        // when, then
        final ExtractableResponse<Response> response = requestPostStation(STATION_REQUEST_신림역,
            "/stations");

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestPostStation(STATION_REQUEST_신림역,
            "/stations");

        ExtractableResponse<Response> createResponse2 = requestPostStation(STATION_REQUEST_서울대역,
            "/stations");

        // when
        ExtractableResponse<Response> response = requestGetStations("/stations");
        List<Long> expectedStationIds = getExpectedStationIds(createResponse1, createResponse2);
        List<Long> resultStationIds = getResultStationIds(response);

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
        ExtractableResponse<Response> createResponse = requestPostStation(STATION_REQUEST_신림역, "/stations");

        // when
        final ExtractableResponse<Response> response = requestDeleteStation(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
