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
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;

public class StationAcceptanceTest extends AcceptanceTest {

    private static final StationRequest STATION_REQUEST_강남역 = new StationRequest("강남역");
    private static final StationRequest STATION_REQUEST_잠실역 = new StationRequest("잠실역");
    private static final StationRequest STATION_REQUEST_역삼역 = new StationRequest("역삼역");

    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // given & when
        ExtractableResponse<Response> response = requestPostStation(STATION_REQUEST_강남역, "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("중복된 이름으로 지하철역을 생성하면 예외를 발생시킨다.")
    void createStationWithDuplicateName() {
        // given
        requestPostStation(STATION_REQUEST_강남역, "/stations");

        // when, then
        RestAssured.given().log().all()
            .body(STATION_REQUEST_강남역)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then()
            .log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = requestPostStation(STATION_REQUEST_잠실역, "/stations");

        ExtractableResponse<Response> createResponse2 = requestPostStation(STATION_REQUEST_역삼역, "/stations");

        // when
        ExtractableResponse<Response> response = requestGetStations("/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedStationIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath()
            .getList(".", StationResponse.class)
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        StationRequest request = new StationRequest("강남역");

        ExtractableResponse<Response> createResponse = requestPostStation(request, "/stations");

        // when, then
        String uri = createResponse.header("Location");
        RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> requestPostStation(final StationRequest requestBody, final String URI) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(requestBody)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(URI)
            .then().log().all()
            .extract();
        return response;
    }

    private ExtractableResponse<Response> requestGetStations(final String URI) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(URI)
            .then().log().all()
            .extract();
        return response;
    }
}
