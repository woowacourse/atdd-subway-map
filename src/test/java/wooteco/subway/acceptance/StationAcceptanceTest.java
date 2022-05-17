package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // given
        StationRequest stationRequest = new StationRequest("선릉역");

        // when
        ExtractableResponse<Response> response = extractCreateStationRequest(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> extractCreateStationRequest(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 상태코드는 bad request를 반환해야 한다.")
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        extractCreateStationRequest(stationRequest);

        // when
        ExtractableResponse<Response> response = extractCreateStationRequest(stationRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = extractCreateStationRequest(new StationRequest("강남역"));
        ExtractableResponse<Response> createResponse2 = extractCreateStationRequest(new StationRequest("역삼역"));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = getExpectedLineIds(createResponse1, createResponse2);
        List<Long> resultLineIds = getResultLineIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> getResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", StationResponse.class).stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
    }

    private List<Long> getExpectedLineIds(ExtractableResponse<Response> createResponse1,
        ExtractableResponse<Response> createResponse2) {
        return Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    @Test
    @DisplayName("존재하는 지하철역을 제거한다. 상태코드는 200 이어야 한다.")
    void deleteStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = extractCreateStationRequest(stationRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("존재하지 않는 지하철역을 제거한다. 상태코드는 204 이어야 한다.")
    void deleteNonStation() {
        RestAssured.given().log().all()
            .when()
            .delete("stations/1")
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("name을 지정하지 않고 요청하면 bad request 예외를 반환해야 한다.")
    void emptyName() {
        StationRequest stationRequest = new StationRequest(null);
        creatStationRequest(stationRequest)
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    private ValidatableResponse creatStationRequest(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(stationRequest)
            .when()
            .post("/stations")
            .then().log().all();
    }
}
