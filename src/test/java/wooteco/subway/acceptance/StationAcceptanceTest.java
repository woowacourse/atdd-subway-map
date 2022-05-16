package wooteco.subway.acceptance;

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

import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static wooteco.subway.acceptance.AcceptanceFixture.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역 생성")
    @Test
    void createStation() {
        insert(new StationRequest("강남역"), "/stations", 201)
                .header("Location", is("/stations/1"))
                .body("name", is("강남역"));
    }

    @DisplayName("중복된 지하철역 생성")
    @Test
    void createStationWithDuplicateName() {
        // given
        insert(new StationRequest("강남역"), "/stations", 201);

        // then
        RestAssured.given().log().all()
                .body(new StationRequest("강남역"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철역 조회")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> stationResponse = insert(new StationRequest("강남역"), "/stations", 201).extract();
        ExtractableResponse<Response> newStationResponse = insert(new StationRequest("역삼역"), "/stations", 201).extract();

        ExtractableResponse<Response> response = select("/stations", 200).extract();

        // then
        List<Long> expectedLineIds = Arrays.asList(stationResponse, newStationResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역 제거")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> stationResponse = insert(new StationRequest("강남역"),
                "/stations", 201).extract();

        // then
        String uri = stationResponse.header("Location");
        delete(uri, 204);
    }
}
