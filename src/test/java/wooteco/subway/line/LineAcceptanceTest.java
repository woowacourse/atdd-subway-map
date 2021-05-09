package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.StationResponse;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private final List<Long> stationIds = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        super.setUp();

        stationIds.add(postStation("강남역"));
        stationIds.add(postStation("잠실역"));
        stationIds.add(postStation("양재역"));
        stationIds.add(postStation("석촌역"));
        stationIds.add(postStation("판교역"));
        stationIds.add(postStation("교대역"));
    }

    private Long postStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return Long.parseLong(RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract()
                .header("Location").split("/")[2]);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(1)));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = postLine(params);
        LineResponse lineResponse = response.as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(lineResponse.getId()).isNotNull();
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    private ExtractableResponse<Response> postLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "신분당선");
        params2.put("upStationId", String.valueOf(stationIds.get(2)));
        params2.put("downStationId", String.valueOf(stationIds.get(3)));
        params2.put("distance", "10");
        ExtractableResponse<Response> response = postLine(params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선 색으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-red-600");
        params2.put("name", "분당선");
        params2.put("upStationId", String.valueOf(stationIds.get(2)));
        params2.put("downStationId", String.valueOf(stationIds.get(3)));
        params2.put("distance", "10");
        ExtractableResponse<Response> response = postLine(params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> createResponse1 = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        params2.put("upStationId", String.valueOf(stationIds.get(2)));
        params2.put("downStationId", String.valueOf(stationIds.get(3)));
        params2.put("distance", "10");
        ExtractableResponse<Response> createResponse2 = postLine(params2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

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

    @DisplayName("특정 ID의 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        params2.put("upStationId", String.valueOf(stationIds.get(2)));
        params2.put("downStationId", String.valueOf(stationIds.get(3)));
        params2.put("distance", "10");
        postLine(params2);

        // when
        ExtractableResponse<Response> response = getLine(oldResponse.header("Location"));
        LineResponse lineResponse = response.as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getId()).isEqualTo(Long.parseLong(oldResponse.header("Location").split("/")[2]));
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        List<StationResponse> stationResponses = Arrays.asList(new StationResponse(stationIds.get(0), "강남역"),
                new StationResponse(stationIds.get(1), "잠실역"));
        assertThat(lineResponse.getStations()).usingRecursiveComparison().isEqualTo(stationResponses);
    }

    private ExtractableResponse<Response> getLine(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    @DisplayName("존재하지 않는 id의 노선을 조회한다.")
    @Test
    void getLineOfIdDoesNotExist() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        params2.put("upStationId", String.valueOf(stationIds.get(2)));
        params2.put("downStationId", String.valueOf(stationIds.get(3)));
        params2.put("distance", "10");
        postLine(params2);

        // when
        ExtractableResponse<Response> response = getLine("/lines/0");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        ExtractableResponse<Response> response = putLine(oldResponse.header("Location"), params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 id의 노선을 수정한다.")
    @Test
    void updateLineOfIdDoesNotExist() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        ExtractableResponse<Response> response = putLine("/lines/0", params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 존재하는 이름으로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        params2.put("upStationId", String.valueOf(stationIds.get(2)));
        params2.put("downStationId", String.valueOf(stationIds.get(3)));
        params2.put("distance", "10");
        postLine(params2);

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("color", "bg-green-600");
        params3.put("name", "분당선");
        ExtractableResponse<Response> response = putLine(oldResponse.header("Location"), params3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 존재하는 색으로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateColor() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        params2.put("upStationId", String.valueOf(stationIds.get(2)));
        params2.put("downStationId", String.valueOf(stationIds.get(3)));
        params2.put("distance", "10");
        postLine(params2);

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("color", "bg-blue-600");
        params3.put("name", "2호선");
        ExtractableResponse<Response> response = putLine(oldResponse.header("Location"), params3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> putLine(String path, Map<String, String> params3) {
        return RestAssured.given().log().all()
                .when()
                .body(params3)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put(path)
                .then().log().all()
                .extract();
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        // when
        ExtractableResponse<Response> response = deleteLine(oldResponse.header("Location"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> deleteLine(String path) {
        return RestAssured.given().log().all()
                .when()
                .delete(path)
                .then()
                .log().all()
                .extract();
    }

    @DisplayName("id가 존재하지 않는 노선을 삭제한다.")
    @Test
    void deleteLineOfIdDoesNotExist() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        postLine(params1);

        // when
        ExtractableResponse<Response> response = deleteLine("/lines/0");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 상행 종점 구간을 추가 한다.")
    @Test
    void addFirstSectionToLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("upStationId", String.valueOf(stationIds.get(2)));
        params2.put("downStationId", String.valueOf(stationIds.get(0)));
        params2.put("distance", "10");
        ExtractableResponse<Response> response = postSection(oldResponse.header("Location"), params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선에 하행 종점 구간을 추가 한다.")
    @Test
    void addLastSectionToLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("upStationId", String.valueOf(stationIds.get(1)));
        params2.put("downStationId", String.valueOf(stationIds.get(2)));
        params2.put("distance", "10");
        ExtractableResponse<Response> response = postSection(oldResponse.header("Location"), params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선의 구간 사이에 왼쪽 구간을 추가 한다.")
    @Test
    void addLeftSectionInBetweenToLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("upStationId", String.valueOf(stationIds.get(1)));
        params2.put("downStationId", String.valueOf(stationIds.get(2)));
        params2.put("distance", "10");
        postSection(oldResponse.header("Location"), params2);

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("upStationId", String.valueOf(stationIds.get(1)));
        params3.put("downStationId", String.valueOf(stationIds.get(3)));
        params3.put("distance", "5");
        ExtractableResponse<Response> response = postSection(oldResponse.header("Location"), params3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> postSection(String path, Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path + "/sections")
                .then()
                .log().all()
                .extract();
    }

    @DisplayName("노선의 구간 사이에 오른쪽 구간을 추가 한다.")
    @Test
    void addRightSectionInBetweenToLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("upStationId", String.valueOf(stationIds.get(1)));
        params2.put("downStationId", String.valueOf(stationIds.get(2)));
        params2.put("distance", "10");
        postSection(oldResponse.header("Location"), params2);

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("upStationId", String.valueOf(stationIds.get(3)));
        params3.put("downStationId", String.valueOf(stationIds.get(1)));
        params3.put("distance", "5");
        ExtractableResponse<Response> response = postSection(oldResponse.header("Location"), params3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선의 구간 사이에 기존 구간과 같은 길이의 왼쪽 구간을 추가 한다.")
    @Test
    void addLeftSectionInBetweenToLineWithSameLength() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("upStationId", String.valueOf(stationIds.get(1)));
        params2.put("downStationId", String.valueOf(stationIds.get(2)));
        params2.put("distance", "10");
        postSection(oldResponse.header("Location"), params2);

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("upStationId", String.valueOf(stationIds.get(1)));
        params3.put("downStationId", String.valueOf(stationIds.get(3)));
        params3.put("distance", "10");
        ExtractableResponse<Response> response = postSection(oldResponse.header("Location"), params3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선의 구간 사이에 기존 구간보다 긴 오른쪽 구간을 추가 한다.")
    @Test
    void addRightSectionInBetweenToLineWithLongerLength() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationIds.get(0)));
        params1.put("downStationId", String.valueOf(stationIds.get(1)));
        params1.put("distance", "10");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("upStationId", String.valueOf(stationIds.get(1)));
        params2.put("downStationId", String.valueOf(stationIds.get(2)));
        params2.put("distance", "10");
        postSection(oldResponse.header("Location"), params2);

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("upStationId", String.valueOf(stationIds.get(3)));
        params3.put("downStationId", String.valueOf(stationIds.get(1)));
        params3.put("distance", "20");
        ExtractableResponse<Response> response = postSection(oldResponse.header("Location"), params3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
