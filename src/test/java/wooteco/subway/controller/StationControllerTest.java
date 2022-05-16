package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.acceptance.AcceptanceTest;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;

class StationControllerTest extends AcceptanceTest {

    private final Station testStation7 = new Station("testStation7");
    private final Station testStation7DuplicatedName = new Station("testStation7");
    private final Station testStation8 = new Station("testStation8");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        RestAssured.
                given().log().all().
                    body(testStation7).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().log().all().
                    statusCode(HttpStatus.CREATED.value()).
                    header("Location", is(not("")));
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        RestAssured.
                given().log().all().
                    body(testStation7).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().
                    extract();

        // when
        RestAssured.
                given().log().all().
                    body(testStation7DuplicatedName).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().
                    statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.
                given().log().all().
                    body(testStation7).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().
                    extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.
                given().log().all().
                    body(testStation8).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().
                    extract();

        // when
        ExtractableResponse<Response> response = RestAssured.
                given().log().all().
                when().
                    get("/stations").
                then().
                    statusCode(HttpStatus.OK.value()).
                extract();

        // then
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.
                given().log().all().
                    body(testStation7).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().
                    extract();

        // when
        String uri = createResponse.header("Location");
        RestAssured.
                given().log().all().
                when().
                    delete(uri).
                then().
                    statusCode(HttpStatus.NO_CONTENT.value());
    }
}
