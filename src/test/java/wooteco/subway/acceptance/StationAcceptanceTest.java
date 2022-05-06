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

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        // then
        RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("강남역"))
                .header("Location", "/stations/1");
    }

    @DisplayName("중복된 지하철역을 생성")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("강남역"));

        // then
        RestAssured.given().log().all()
                .body(stationRequest)
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
        StationRequest stationRequest = new StationRequest("강남역");
        StationRequest newStationRequest = new StationRequest("역삼역");

        // when
        ExtractableResponse<Response> stationResponse = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("강남역"))
                .extract();

        ExtractableResponse<Response> newStationResponse = RestAssured.given().log().all()
                .body(newStationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("역삼역"))
                .extract();

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
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> stationResponse = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", is("강남역"))
                .extract();

        // then
        String uri = stationResponse.header("Location");
        RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }
}
