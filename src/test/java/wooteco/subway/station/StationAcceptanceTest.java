package wooteco.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.presentation.line.dto.LineRequest;
import wooteco.subway.presentation.station.dto.StationRequest;
import wooteco.subway.presentation.station.dto.StationResponse;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@Sql("classpath:/station/stationQueryInit.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StationAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("참조중인 역을 지울 수 없다")
    @Test
    void deleteStation_exceptionWhenIsReferenced() {
        ExtractableResponse<Response> 강남역 = createNewStation("강남역");
        createNewStation("서울역");

        insertLine("분당선", "Yello", 1L, 2L, 10L);


        String uri = 강남역.header("Location");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract();
    }

    private void insertLine(String name, String color, Long upStationId, Long downStationId, Long distance) {
        RestAssured
                .given()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new LineRequest(name, color, upStationId, downStationId, distance))
                .post("/lines");
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = createNewStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        createNewStation("강남역");

        // when
        ExtractableResponse<Response> response = createNewStation("강남역");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response> 강남역_response = createNewStation("강남역");
        ExtractableResponse<Response> 역삼역_response = createNewStation("역삼역");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/stations")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        List<Long> expectedLineIds = extractLineIdsFromDummyResponse(강남역_response, 역삼역_response);
        List<Long> resultLineIds = extractLineIdsFromResponse(response);

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> extractLineIdsFromResponse(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(toList());
    }

    private List<Long> extractLineIdsFromDummyResponse(ExtractableResponse<Response> 강남역_response,
                                                       ExtractableResponse<Response> 역삼역_response) {
        return Stream.of(강남역_response, 역삼역_response)
                .map(resp -> Long.parseLong(resp.header("Location").split("/")[2]))
                .collect(toList());
    }

    private ExtractableResponse<Response> createNewStation(String name) {
        return RestAssured
                .given()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new StationRequest(name))
                .post("/stations")
                .then()
                .extract();
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> createResponse = createNewStation("강남역");

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }
}
