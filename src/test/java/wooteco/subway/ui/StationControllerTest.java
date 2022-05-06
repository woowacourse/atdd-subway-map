package wooteco.subway.ui;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StationControllerTest {

    private Station testStation1 = new Station("강남역");
    private Station testStation2 = new Station("역삼역");

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = RestAssured.
                given().log().all().
                    body(testStation1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().
                    extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        RestAssured.
                given().log().all().
                    body(testStation1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().
                    extract();

        // when
        RestAssured.
                given().log().all().
                    body(testStation1).
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
                    body(testStation1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/stations").
                then().
                    extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.
                given().log().all().
                    body(testStation2).
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
                    extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
                    body(testStation1).
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
