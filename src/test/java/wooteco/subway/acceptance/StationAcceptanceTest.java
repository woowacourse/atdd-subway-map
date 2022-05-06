package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.controller.dto.station.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 역을 생성한다.")
    @Test
    void createStation() {
        ExtractableResponse<Response> response = saveStation(Map.of("name", "선릉역"));

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
        });
    }

    @DisplayName("기존에 존재하는 지하철 역 이름으로 지하철 역을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"강남역", "선릉역"})
    void createStationWithDuplicateName(String name) {
        saveStation(Map.of("name", name));

        ExtractableResponse<Response> response = saveStation(Map.of("name", name));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 역 목록을 조회한다.")
    @Test
    void getStations() {
        Long createdId1 = extractId(saveStation(Map.of("name", "강남역")));
        Long createdId2 = extractId(saveStation(Map.of("name", "선릉역")));

        ExtractableResponse<Response> response = findStations();

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(extractIds(response)).containsAll(List.of(createdId1, createdId2));
        });
    }

    @DisplayName("지하철 역을 제거한다.")
    @Test
    void deleteStation() {
        Long createdId = extractId(saveStation(Map.of("name", "강남역")));

        ExtractableResponse<Response> response = removeStation(createdId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> saveStation(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findStations() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> removeStation(Long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/stations/" + id)
                .then().log().all()
                .extract();
    }

    private Long extractId(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getObject(".", StationResponse.class)
                .getId();
    }

    private List<Long> extractIds(ExtractableResponse<Response> response) {
        return response.jsonPath()
                .getList(".", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toUnmodifiableList());
    }
}
