package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.response.StationResponse;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        final ExtractableResponse<Response> response = createStation("강남역");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        createStation("강남역");
        final ExtractableResponse<Response> response = createStation("강남역");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    Stream<DynamicTest> getStationsTest() {
        final ExtractableResponse<Response> createResponse1 = createStation("강남역");
        final ExtractableResponse<Response> createResponse2 = createStation("역삼역");

        return Stream.of(
                DynamicTest.dynamicTest("생성된 지하철역 목록을 불러온다.", () -> {
                    final ExtractableResponse<Response> response = getStations();
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                DynamicTest.dynamicTest("생성된 지하철역 목록이 저장한 목록과 일치한지 확인한다.", () -> {
                    final ExtractableResponse<Response> response = getStations();
                    final List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                            .collect(Collectors.toList());
                    final List<Long> resultLineIds = response.jsonPath().getList(".", StationResponse.class).stream()
                            .map(StationResponse::getId)
                            .collect(Collectors.toList());
                    assertThat(resultLineIds).containsAll(expectedLineIds);
                })
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        final ExtractableResponse<Response> createResponse = createStation("강남역");

        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createStation(final String name) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getStations() {
        return RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

}
