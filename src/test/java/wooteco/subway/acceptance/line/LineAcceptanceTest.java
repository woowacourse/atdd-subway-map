package wooteco.subway.acceptance.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.acceptance.AcceptanceTest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final List<Long> stationIds = new ArrayList<>();
    private ExtractableResponse<Response> createdResponse;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        stationIds.add(postStation("강남역"));
        stationIds.add(postStation("잠실역"));
        stationIds.add(postStation("양재역"));
        stationIds.add(postStation("석촌역"));
        stationIds.add(postStation("판교역"));
        stationIds.add(postStation("교대역"));

        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(1)));
        params.put("distance", "10");

        createdResponse = postLine(params);
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
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");
        params.put("upStationId", String.valueOf(stationIds.get(2)));
        params.put("downStationId", String.valueOf(stationIds.get(3)));
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = postLine(params);
        LineResponse lineResponse = response.as(LineResponse.class);

        // then
        List<StationResponse> expect = Arrays.asList(new StationResponse(stationIds.get(2), "양재역"),
            new StationResponse(stationIds.get(3), "석촌역"));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(lineResponse.getId()).isNotNull();
        assertThat(lineResponse.getColor()).isEqualTo("bg-blue-600");
        assertThat(lineResponse.getName()).isEqualTo("분당선");
        assertThat(lineResponse.getStations()).usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(expect);
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "신분당선");
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = postLine(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선 색으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "분당선");
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = postLine(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재 하지 않는 ID의 역을 상행 또는 하행 종점역으로 사용한다.")
    @Test
    void createLineWithInvalidStationId() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");
        params.put("upStationId", "999");
        params.put("downStationId", String.valueOf(stationIds.get(1)));
        params.put("distance", "10");
        ExtractableResponse<Response> response = postLine(params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("같은 상행 종점역과 하행 종점역으로 사용한다.")
    @Test
    void createLineWithDuplicateStationId() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(0)));
        params.put("distance", "10");
        ExtractableResponse<Response> response = postLine(params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("자연수가 아닌 거리로 노선을 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "14.2", "십"})
    void createLineWithNoneNaturalNumberDistance(String distance) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(1)));
        params.put("distance", distance);

        //when
        ExtractableResponse<Response> response = postLine(params);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");
        ExtractableResponse<Response> createdResponse2 = postLine(params);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createdResponse, createdResponse2)
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
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(createdResponse.header("Location"))
            .then().log().all()
            .extract();
        LineResponse lineResponse = response.as(LineResponse.class);

        // then
        List<StationResponse> expect = Arrays.asList(new StationResponse(stationIds.get(0), "강남역"),
            new StationResponse(stationIds.get(1), "잠실역"));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getId())
            .isEqualTo(Long.parseLong(createdResponse.header("Location").split("/")[2]));
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getStations()).usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(expect);
    }

    @DisplayName("존재하지 않는 ID의 노선을 조회한다.")
    @Test
    void getLineOfIdDoesNotExist() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/0")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(createdResponse.header("Location"))
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 id의 노선을 수정한다.")
    @Test
    void updateLineOfIdDoesNotExist() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .get("/lines/0")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 존재하는 이름으로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given

        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");
        postLine(params);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "분당선");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .put(createdResponse.header("Location"))
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 존재하는 색으로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateColor() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "분당선");
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("color", "bg-blue-600");
        params3.put("name", "2호선");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params3)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .put(createdResponse.header("Location"))
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(createdResponse.header("Location"))
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("id가 존재하지 않는 노선을 삭제한다.")
    @Test
    void deleteLineOfIdDoesNotExist() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/0")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void createSection() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "5");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        LineResponse lineResponse = RestAssured.given().log().all()
            .when()
            .get(createdResponse.header("Location"))
            .then()
            .extract()
            .as(LineResponse.class);

        // then
        List<StationResponse> expected = Arrays.asList(
            new StationResponse(stationIds.get(0), "강남역"),
            new StationResponse(stationIds.get(2), "양재역"),
            new StationResponse(stationIds.get(1), "잠실역")
        );
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getStations()).usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(expected);
    }

    @DisplayName("노선에 없는 역들로 구간을 추가한다.")
    @Test
    void createSectionWithStationsNotInLine() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(2)));
        params.put("downStationId", String.valueOf(stationIds.get(3)));
        params.put("distance", "5");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("같은 상행역과 하행역으로 구간을 추가한다.")
    @Test
    void createSectionWithSameStations() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(1)));
        params.put("distance", "5");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행 종점역이나 하행 종점역에 구간 추가한다.")
    @Test
    void createSectionAtEndStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(1)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "5");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        LineResponse lineResponse = RestAssured.given().log().all()
            .when()
            .get(createdResponse.header("Location"))
            .then()
            .extract()
            .as(LineResponse.class);

        // then
        List<StationResponse> expected = Arrays.asList(
            new StationResponse(stationIds.get(0), "강남역"),
            new StationResponse(stationIds.get(1), "잠실역"),
            new StationResponse(stationIds.get(2), "양재역")
        );
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getStations()).usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(expected);
    }

    @DisplayName("기존 구간보다 길거나 같은 구간을 사이에 추가한다.")
    @Test
    void createSectionWithLongerDistance() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "10");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재 하지않는 ID의 노선에 구간을 등록한다.")
    @Test
    void createSectionOnLineWithInvalidId() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "10");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post("/lines/999/sections")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재 하지않는 ID의 역으로 구간을 등록한다.")
    @Test
    void createSectionWithInvalidIdOfStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", "999");
        params.put("distance", "5");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("자연수가 아닌 구간의 길이를 등록한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "오"})
    void createSectionWithNotDistanceOfNaturalNumber(String invalidDistance) {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", invalidDistance);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 구간을 제거한다.(가운데 있는 역 삭제)")
    @Test
    void deleteSection() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "5");
        RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(createdResponse.header("Location") + "/sections?stationId=" + stationIds.get(2))
            .then()
            .extract();

        LineResponse lineResponse = RestAssured.given().log().all()
            .when()
            .get(createdResponse.header("Location"))
            .then()
            .extract()
            .as(LineResponse.class);

        // then
        List<StationResponse> expected = Arrays.asList(
            new StationResponse(stationIds.get(0), "강남역"),
            new StationResponse(stationIds.get(1), "잠실역")
        );
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineResponse.getStations()).usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(expected);
    }

    @DisplayName("지하철 구간을 제거한다.(종점역 삭제)")
    @Test
    void deleteSectionWithEndStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "5");
        RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(createdResponse.header("Location") + "/sections")
            .then()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(createdResponse.header("Location") + "/sections?stationId=" + stationIds.get(0))
            .then()
            .extract();

        LineResponse lineResponse = RestAssured.given().log().all()
            .when()
            .get(createdResponse.header("Location"))
            .then()
            .extract()
            .as(LineResponse.class);

        // then
        List<StationResponse> expected = Arrays.asList(
            new StationResponse(stationIds.get(2), "양재역"),
            new StationResponse(stationIds.get(1), "잠실역")
        );
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(lineResponse.getStations()).usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 ID를 가진 노선의 구간을 삭제한다.")
    @Test
    void deleteSectionWithInvalidLineId() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("lines/999/sections?stationId=" + stationIds.get(0))
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
