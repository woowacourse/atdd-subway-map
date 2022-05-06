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
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final String PATH_PREFIX = "/stations";
    private static final String LOCATION = "Location";

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

    private ExtractableResponse<Response> createStation(final StationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(PATH_PREFIX)
                .then().log().all()
                .extract();
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
        final ExtractableResponse<Response> response1 = createStation(gangNamStationRequest);
        final ExtractableResponse<Response> response2 = createStation(jamSilStationRequest);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .get(PATH_PREFIX)
                .then().log().all()
                .extract();

        // then
        final List<Long> expectedLineIds = Stream.of(response1, response2)
                .map(this::extractId)
                .collect(Collectors.toList());
        final List<Long> actualLineIds = actual.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(actualLineIds).containsAll(expectedLineIds);
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private long extractId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header(LOCATION).split("/")[2]);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void DeleteStation() {
        // given
        final ExtractableResponse<Response> createResponse = createStation(gangNamStationRequest);
        final long id = extractId(createResponse);

        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .delete(PATH_PREFIX + "/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 역을 제거하면 BadRequest 를 반환한다.")
    @Test
    void DeleteStation_NotExistId_BadRequest() {
        // when
        final ExtractableResponse<Response> actual = RestAssured.given().log().all()
                .when()
                .delete(PATH_PREFIX + "/999")
                .then().log().all()
                .extract();

        // then
        assertThat(actual.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
