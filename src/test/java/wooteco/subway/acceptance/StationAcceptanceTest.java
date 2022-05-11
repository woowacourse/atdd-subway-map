package wooteco.subway.acceptance;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private <T> ExtractableResponse<Response> insert(T request, String path) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        ExtractableResponse<Response> response = insert(new StationRequest("강남역"), "/stations");

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("강남역");
        assertThat(response.header("Location")).isEqualTo("/stations/1");
    }

    @DisplayName("중복된 지하철역을 생성")
    @Test
    void createStationWithDuplicateName() {
        // given
        insert(new StationRequest("강남역"), "/stations");

        // then
        RestAssured.given().log().all()
                .body(new StationRequest("강남역"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> stationResponse = insert(new StationRequest("강남역"), "/stations");
        ExtractableResponse<Response> newStationResponse = insert(new StationRequest("역삼역"), "/stations");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        // then
        List<Long> expectedLineIds = Arrays.asList(stationResponse, newStationResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> stationResponse = insert(new StationRequest("강남역"), "/stations");

        // then
        String uri = stationResponse.header("Location");
        RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
