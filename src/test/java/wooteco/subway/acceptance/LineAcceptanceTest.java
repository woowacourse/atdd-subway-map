package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.StationAcceptanceTest.STATIONS_URI;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LINES_URI = "/lines";

    @BeforeEach
    void init() {
        createNewStation("신림역");
        createNewStation("봉천역");
        createNewStation("서울대입구역");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "GREEN", 1L, 2L, 5);
        // when
        ExtractableResponse<Response> response = postRequest(LINES_URI, lineRequest);
        // then
        List<StationResponse> responseStations = response.jsonPath().getList("stations", StationResponse.class);
        assertAll(
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("GREEN"),
                () -> assertThat(responseStations).hasSize(2),
                () -> assertThat(responseStations.get(0).getName()).isEqualTo("신림역"),
                () -> assertThat(responseStations.get(1).getName()).isEqualTo("봉천역"),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "GREEN", 1L, 2L, 5);
        postRequest(LINES_URI, lineRequest);
        // when
        ExtractableResponse<Response> response = postRequest(LINES_URI, lineRequest);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선들을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = postRequest(LINES_URI,
                new LineRequest("2호선", "GREEN", 1L, 2L, 5));
        ExtractableResponse<Response> createResponse2 = postRequest(LINES_URI,
                new LineRequest("3호선", "GREEN", 1L, 2L, 5));
        // when
        ExtractableResponse<Response> response = getRequest(LINES_URI);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("id를 통해 지하철 노선을 조회한다.")
    @Test
    void getLineById() {
        // given
        ExtractableResponse<Response> createdResponse = postRequest(LINES_URI,
                new LineRequest("6호선", "SKYBLUE", 1L, 2L, 5));
        String uri = createdResponse.header("Location");
        // when
        ExtractableResponse<Response> response = getRequest(uri);
        // then
        assertAll(
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("6호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("SKYBLUE"),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given

        ExtractableResponse<Response> createResponse = postRequest(LINES_URI,
                new LineRequest("2호선", "GREEN", 1L, 2L, 5));
        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        ExtractableResponse<Response> createdResponse = postRequest(LINES_URI,
                new LineRequest("2호선", "GREEN", 1L, 2L, 5));
        String uri = createdResponse.header("Location");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "10호선");
        params2.put("color", "ORANGE");
        // when
        ExtractableResponse<Response> response = putRequest(uri, params2);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 ID의 노선을 수정한다.")
    @Test
    void updateLine_noExistLine_Exception() {
        // given
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "10호선");
        params2.put("color", "ORANGE");
        // when
        ExtractableResponse<Response> response = putRequest("/lines/10000", params2);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중복된 이름으로 노선을 수정한다.")
    @Test
    void updateLine_duplicateName_Exception() {
        // given
        postRequest(LINES_URI, new LineRequest("2호선", "GREEN", 1L, 2L, 5));
        ExtractableResponse<Response> createdResponse = postRequest(LINES_URI,
                new LineRequest("3호선", "GREEN", 1L, 2L, 5));
        String uri = createdResponse.header("Location");

        ExtractableResponse<Response> response = putRequest(uri,
                Map.of("name", "2호선", "color", "ORANGE"));
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "GREEN", 1L, 2L, 5);
        postRequest(LINES_URI, lineRequest);

        SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);
        // when
        ExtractableResponse<Response> response = postRequest("/lines/1/sections", sectionRequest);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // TODO 조회기능이 완성되면 조회해서 잘 들어갔는지 확인한다.
    }

    private ExtractableResponse<Response> postRequest(String path, Object body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getRequest(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> putRequest(String path, Map<String, String> body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put(path)
                .then().log().all()
                .extract();
    }

    private void createNewStation(String name) {
        Map<String, String> params = new HashMap<>(Map.of("name", name));
        RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post(STATIONS_URI);
    }
}
