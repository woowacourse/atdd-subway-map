package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.station.StationRequest;
import wooteco.subway.dto.station.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    private final StationRequest gangNamStationRequest = new StationRequest("강남역");
    private final StationRequest jamSilStationRequest = new StationRequest("잠실역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void CreateStation() {
        // when
        final ExtractableResponse<Response> response = createStation(gangNamStationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(LOCATION)).isNotBlank();
    }

    @DisplayName("생성하려는 역의 이름이 중복되면 BadRequest 를 반환한다.")
    @Test
    void CreateStation_DuplicateName_BadRequest() {
        // given
        createStation(gangNamStationRequest);

        // when
        final ExtractableResponse<Response> response = createStation(gangNamStationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void ShowStations() {
        /// given
        final ExtractableResponse<Response> expected1 = createStation(gangNamStationRequest);
        final ExtractableResponse<Response> expected2 = createStation(jamSilStationRequest);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(STATION_PATH_PREFIX)
                .then().log().all()
                .extract();

        // then
        final List<Long> expectedLineIds = Stream.of(expected1, expected2)
                .map(this::extractId)
                .collect(Collectors.toList());

        final List<Long> actualLineIds = actual.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void DeleteStation() {
        // given
        final long id = createAndGetStationId(gangNamStationRequest);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .delete(STATION_PATH_PREFIX + SLASH + id)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 역을 제거하면 404를 반환한다.")
    @Test
    void DeleteStation_NotExistId_BadRequest() {
        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .delete(STATION_PATH_PREFIX + SLASH + 999)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
