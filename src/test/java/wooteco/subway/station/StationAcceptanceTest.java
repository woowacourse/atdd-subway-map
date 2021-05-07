package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.domain.station.Station;
import wooteco.subway.web.dto.LineResponse;

@DisplayName("역 인수 테스트")
public class StationAcceptanceTest extends AcceptanceTest {

    private static final Map<String, String> params1 = new HashMap<>();
    private static final Map<String, String> params2 = new HashMap<>();

    static {
        params1.put("name", "잠실역");
        params2.put("name", "강남역");
    }

    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStation() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("중복된 지하철 역을 생성할 수 없다.")
    void cannotCreateDuplicatedStation() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철역을 조회한다.")
    void showStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> listResponse = getStationListResponse();

        // then
        assertThat(createResponse1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse2.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(listResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Station> expectedStations = Stream.of(createResponse1, createResponse2)
                .map(response -> new Station(
                        response.jsonPath().getLong("id"),
                        response.jsonPath().getString("name")
                ))
                .collect(Collectors.toList());

        List<Station> resultStations = listResponse.jsonPath().getList(".", Station.class);

        for (int i = 0; i < resultStations.size(); i++) {
            Station result = resultStations.get(i);
            Station expected = expectedStations.get(i);
            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getName()).isEqualTo(expected.getName());
        }
    }

    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> stationListResponse = getStationListResponse();
        assertThat(stationListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> lineResponses = stationListResponse.jsonPath().getList(".", LineResponse.class);
        assertThat(lineResponses.size()).isEqualTo(1);
        assertThat(lineResponses.get(0).getName()).isEqualTo(params2.get("name"));
    }

    private ExtractableResponse<Response> getStationListResponse() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }
}
