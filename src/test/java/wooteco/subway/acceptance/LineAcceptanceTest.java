package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLineAfterCreatingStation() {
        final ExtractableResponse<Response> response = createLineAfterCreatingStation("신분당선", "bg-red-600",
                "a", "b", 5);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선 생성 시도 시 Bad request가 응답된다.")
    @Test
    void createLineWithDuplicateName() {
        createLineAfterCreatingStation("8호선", "bg-red-600", "a", "b", 5);
        final ExtractableResponse<Response> response = createLineAfterCreatingStation("8호선", "bg-red-600", "c", "d", 5);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @TestFactory
    Stream<DynamicTest> getLinesTest() {
        final ExtractableResponse<Response> createResponse1 = createLineAfterCreatingStation("1호선", "bg-red-600",
                "a", "B", 10);
        final ExtractableResponse<Response> createResponse2 = createLineAfterCreatingStation("2호선", "bg-green-600",
                "c", "d", 10);

        return Stream.of(
                DynamicTest.dynamicTest("생성된 노선 목록을 불러온다", () -> {
                    final ExtractableResponse<Response> response = getLines();

                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                }),
                DynamicTest.dynamicTest("생성된 노선이 저장한 노선과 일치한지 확인한다.", () -> {
                    final ExtractableResponse<Response> response = getLines();
                    final List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                            .collect(Collectors.toList());
                    final List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                            .map(it -> it.getId())
                            .collect(Collectors.toList());

                    assertThat(resultLineIds).containsAll(expectedLineIds);
                })
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        final ExtractableResponse<Response> createResponse = createLineAfterCreatingStation("1호선", "bg-red-600",
                "a", "b", 10);

        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void getLine() {
        final ExtractableResponse<Response> createResponse = createLineAfterCreatingStation("1호선", "bg-red-600",
                "a", "b", 10);
        final LineResponse expected = createResponse.jsonPath().getObject(".", LineResponse.class);

        final ExtractableResponse<Response> response = getLine(createResponse.header("Location"));
        final LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }

    @DisplayName("노선을 갱신한다.")
    @Test
    void updateLine() {
        final ExtractableResponse<Response> createResponse = createLineAfterCreatingStation("1호선", "bg-red-600",
                "a", "b", 10);
        final LineResponse expected = createResponse.jsonPath().getObject(".", LineResponse.class);

        final String newLineName = "11호선";
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = updateLine(newLineName, "bg-red-600", uri);

        final ExtractableResponse<Response> updatedResponse = getLine(uri);
        final LineResponse actual = updatedResponse.jsonPath().getObject(".", LineResponse.class);

        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                () -> assertThat(actual.getName()).isEqualTo(newLineName),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    private ExtractableResponse<Response> updateLine(final String name, final String color, final String uri) {
        final Map<String, String> newParams = new HashMap<>();
        newParams.put("name", name);
        newParams.put("color", "bg-red-600");
        return RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

    }

    private ExtractableResponse<Response> createLineAfterCreatingStation(final String name, final String color,
                                                                         final String upStationName,
                                                                         final String downStationName,
                                                                         final int distance) {
        final ExtractableResponse<Response> stationResponse1 = createStation(upStationName);
        final ExtractableResponse<Response> stationResponse2 = createStation(downStationName);
        final Long upStationId = stationResponse1.jsonPath().getObject(".", StationResponse.class).getId();
        final Long downStationId = stationResponse2.jsonPath().getObject(".", StationResponse.class).getId();
        final Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLine(final String url) {
        return RestAssured.given().log().all()
                .when()
                .get(url)
                .then().log().all()
                .extract();
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

}
