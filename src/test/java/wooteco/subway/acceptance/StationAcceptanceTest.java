package wooteco.subway.acceptance;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ResponseBodyExtractionOptions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        String name = "강남역";

        ExtractableResponse<Response> response = generateStation(name);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        String name = "강남역";
        generateStation(name);

        ExtractableResponse<Response> response = generateStation(name);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        ExtractableResponse<Response> createdResponse1 = generateStation("강남역");
        ExtractableResponse<Response> createdResponse2 = generateStation("역삼역");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        List<Long> expectedStationIds = List.of(createdResponse1, createdResponse2)
                .stream()
                .map(ExtractableResponse::response)
                .map(ResponseBodyExtractionOptions::jsonPath)
                .map(it -> it.getLong("id"))
                .collect(toList());
        List<Long> resultStationIds = response.jsonPath()
                .getList(".", StationResponse.class)
                .stream()
                .map(it -> it.getId())
                .collect(toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultStationIds).containsAll(expectedStationIds)
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        ExtractableResponse<Response> createdResponse = generateStation("강남역");

        String uri = createdResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> generateStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
