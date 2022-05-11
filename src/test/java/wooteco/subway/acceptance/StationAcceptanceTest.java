package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = createParam("강남역");

        // when
        ExtractableResponse<Response> response = createStation(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Map<String, String> params1 = createParam("강남역");
        ExtractableResponse<Response> createResponse1 = createStation(params1);

        Map<String, String> params2 = createParam("역삼역");
        ExtractableResponse<Response> createResponse2 = createStation(params2);

        // when
        ExtractableResponse<Response> response = getAllStations();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedStationIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = createParam("강남역");
        ExtractableResponse<Response> createResponse = createStation(params);

        // when
        Long createdId = getIdFromResponse(createResponse);
        ExtractableResponse<Response> response = deleteStation(createdId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성할 경우 예외를 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        Map<String, String> params = createParam("강남역");
        createStation(params);

        // when
        ExtractableResponse<Response> response = createStation(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않은 지하철 노선을 제거하려 할 경우 NOT FOUND가 반환된다.")
    @Test
    void deleteLine_returnsBadRequestWithNotExistingId() {
        // given & when
        ExtractableResponse<Response> response = deleteStation(3L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> createStation(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> getAllStations() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteStation(Long createdId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/stations/" + createdId)
                .then().log().all()
                .extract();
    }

    private Map<String, String> createParam(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return params;
    }

    private Long getIdFromResponse(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("stations/")[1]);
    }
}
