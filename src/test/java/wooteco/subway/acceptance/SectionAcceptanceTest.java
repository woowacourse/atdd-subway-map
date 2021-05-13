package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

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

    private ExtractableResponse<Response> postSection(Map<String, String> params, String location) {
        return RestAssured.given().log().all()
            .when()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .post(location)
            .then()
            .extract();
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void createSection() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "5");
        ExtractableResponse<Response> response = postSection(params,
            createdResponse.header("Location") + "/sections");

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
        ExtractableResponse<Response> response = postSection(params,
            createdResponse.header("Location") + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("추가할 수 없는 구간입니다.");
    }

    @DisplayName("이미 존재하는 상행역과 하행역을 가진 구간을 추가한다.")
    @Test
    void createSectionWithSameStations() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(1)));
        params.put("distance", "5");
        ExtractableResponse<Response> response = postSection(params,
            createdResponse.header("Location") + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("이미 두 역 모두 노선에 등록되어 있습니다.");
    }

    @DisplayName("상행 종점역이나 하행 종점역에 구간 추가한다.")
    @Test
    void createSectionAtEndStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(1)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "5");
        ExtractableResponse<Response> response = postSection(params,
            createdResponse.header("Location") + "/sections");

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

    @DisplayName("이미 두 역 모두 가지고 있는 노선에 등록을 하면 예외 처리한다.")
    @Test
    void createSectionInLineAlreadyHasAllSections() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(1)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "5");
        postSection(params,
            createdResponse.header("Location") + "/sections");

        Map<String, String> params2 = new HashMap<>();
        params2.put("upStationId", String.valueOf(stationIds.get(0)));
        params2.put("downStationId", String.valueOf(stationIds.get(2)));
        params2.put("distance", "9");
        ExtractableResponse<Response> response = postSection(params,
            createdResponse.header("Location") + "/sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("이미 두 역 모두 노선에 등록되어 있습니다.");
    }

    @DisplayName("기존 구간보다 길거나 같은 구간을 사이에 추가한다.")
    @Test
    void createSectionWithLongerDistance() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "10");
        ExtractableResponse<Response> response = postSection(params,
            createdResponse.header("Location") + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("거리 값은 자연수 입니다.");
    }

    @DisplayName("존재 하지않는 ID의 노선에 구간을 등록한다.")
    @Test
    void createSectionOnLineWithInvalidId() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "10");
        ExtractableResponse<Response> response = postSection(params, "/lines/999/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("존재하지 않는 노선 ID입니다.");
    }

    @DisplayName("존재 하지않는 ID의 역으로 구간을 등록한다.")
    @Test
    void createSectionWithInvalidIdOfStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", "999");
        params.put("distance", "5");
        ExtractableResponse<Response> response = postSection(params,
            createdResponse.header("Location") + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("존재하지 않는 역ID입니다.");
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
        ExtractableResponse<Response> response = postSection(params,
            createdResponse.header("Location") + "/sections");

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
        postSection(params, createdResponse.header("Location") + "/sections");

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
        postSection(params, createdResponse.header("Location") + "/sections");

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
        assertThat(response.asString()).isEqualTo("존재하지 않는 노선 ID입니다.");
    }

    @DisplayName("노선에 존재하지 않는 역의 ID로 구간을 삭제한다.")
    @Test
    void deleteSectionWithInvalidStationId() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationIds.get(0)));
        params.put("downStationId", String.valueOf(stationIds.get(2)));
        params.put("distance", "5");
        postSection(params, createdResponse.header("Location") + "/sections");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(createdResponse.header("Location") + "/sections?stationId=" + stationIds.get(4))
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("존재하지 않는 역입니다.");
    }

    @DisplayName("하나의 구간이 남은 노선에 구간을 삭제한다.")
    @Test
    void deleteOnlySectionInLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(createdResponse.header("Location") + "/sections?stationId=" + stationIds.get(0))
            .then()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).isEqualTo("구간이 하나 남은 경우 삭제할 수 없습니다.");
    }
}
