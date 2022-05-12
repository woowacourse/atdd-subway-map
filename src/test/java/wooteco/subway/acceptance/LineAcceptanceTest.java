package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.DbLineDao;
import wooteco.subway.domain.Station;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static wooteco.subway.acceptance.utils.LineAcceptanceTestFixture.*;
import static wooteco.subway.acceptance.utils.StationAcceptanceTestFixture.역_생성_요청;

@DisplayName("지하철 노선 E2E")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private DbLineDao lineDao;

    @BeforeEach
    void beforeEach() {
        lineDao.deleteAll();
    }

    @DisplayName("지하철 노선 이름에 빈 문자열을 사용할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void createLineWithEmptyName(String lineName) {
        // given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", lineName);
        requestBody.put("color", "bg-red-600");

        // when
        ValidatableResponse response = 노선_생성_요청(requestBody);

        // then
        response.statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 색깔에 빈 문자열을 사용할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void createLineWithEmptyColor(String color) {
        // given
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "신분당선");
        requestBody.put("color", color);

        // when
        ValidatableResponse response = 노선_생성_요청(requestBody);

        // then
        response.statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 정상 등록")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> createStationResponse1 = 역_생성_요청("지하철역");
        long stationId1 = createStationResponse1.jsonPath().getLong("id");

        ExtractableResponse<Response> createStationResponse2 = 역_생성_요청("새로운지하철역");
        Long stationId2 = createStationResponse2.jsonPath().getLong("id");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "신분당선");
        requestBody.put("color", "bg-red-600");
        requestBody.put("upStationId", String.valueOf(stationId1));
        requestBody.put("downStationId", String.valueOf(stationId2));
        requestBody.put("distance", "10");

        // when
        ValidatableResponse response = 노선_생성_요청(requestBody);

        // then
        response
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", not(emptyOrNullString()))
                .body("id", notNullValue())
                .body("name", equalTo("신분당선"))
                .body("color", equalTo("bg-red-600"))
                .body("stations", equalTo(List.of(new Station(stationId1, "지하철역"), new Station(stationId2, "새로운지하철역"))));
    }

    @DisplayName("지하철 노선 중복 등록을 허용하지 않는다")
    @Test
    void createStationWithDuplicateName() {
        // given
        String name = "신분당선";
        String color = "bg-red-600";
        노선_생성_요청(name, color);

        // when
        ExtractableResponse<Response> response = 노선_생성_요청(name, color);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void showLineById() {
        // given
        String name = "신분당선";
        String color = "bg-red-600";
        ExtractableResponse<Response> createResponse = 노선_생성_요청(name, color);
        Long createdId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 노선_목록_조회_요청(createdId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("id")).isNotNull();
        assertThat(response.jsonPath().getString("name")).isEqualTo(name);
        assertThat(response.jsonPath().getString("color")).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 지하철 노선 조회시 예외를 반환한다")
    @Test
    void showNotExistLine() {
        // when
        ExtractableResponse<Response> response = 노선_목록_조회_요청(50L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void showLines() {
        // given
        ExtractableResponse<Response> createResponse1 = 노선_생성_요청("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse2 = 노선_생성_요청("1호선", "bg-blue-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> it.jsonPath().getObject("id", Long.class))
                .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList("id", Long.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(expectedLineIds).containsExactlyInAnyOrderElementsOf(actualLineIds);
    }

    @DisplayName("이름이 공백인 지하철 노선을 수정할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void canNotUpdateLineWithEmptyName(String lineName) {
        // given
        ExtractableResponse<Response> response = 노선_생성_요청("신분당선", "bg-red-600");
        long createdId = response.jsonPath().getLong("id");

        // when & then
        노선_수정_요청(createdId, lineName, "bg-red-600")
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("색깔이 공백인 지하철 노선을 수정할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void canNotUpdateLineWithEmptyColor(String lineColor) {
        // given
        ExtractableResponse<Response> response = 노선_생성_요청("신분당선", "bg-red-600");
        long createdId = response.jsonPath().getLong("id");

        // when & then
        노선_수정_요청(createdId, "신분당선", lineColor)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        String newName = "1호선";
        String newColor = "bg-blue-600";
        ExtractableResponse<Response> createResponse = 노선_생성_요청("신분당선", "bg-red-600");
        long createdId = createResponse.jsonPath().getLong("id");

        // when
        ValidatableResponse updateResponse = 노선_수정_요청(createdId, newName, newColor);

        // then
        updateResponse.statusCode(HttpStatus.OK.value());

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + createdId)
                .then().log().all()
                .extract();

        assertThat(response.jsonPath().getLong("id")).isEqualTo(createdId);
        assertThat(response.jsonPath().getString("name")).isEqualTo(newName);
        assertThat(response.jsonPath().getString("color")).isEqualTo(newColor);
    }

    @DisplayName("중복된 노선 이름 수정을 허용하지 않는다")
    @Test
    void canNotUpdateByDuplicationName() {
        // given
        ExtractableResponse<Response> firstResponse = 노선_생성_요청("신분당선", "bg-yellow-600");
        노선_생성_요청("1호선", "bg-blue-600");

        long createdId = firstResponse.jsonPath().getLong("id");

        노선_수정_요청(createdId, "1호선", "bg-blue-600");

        // when
        노선_수정_요청(createdId, "1호선", "bg-blue-600")
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 노선 수정 시도")
    @Test
    void updateNotExistLine() {
        // when
        RestAssured.given().log().all()
                .body(Map.of("name", "1호선", "color", "bg-red-600"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/50")
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제 시도시 404 반환")
    @Test
    void deleteNotExistLine() {
        // given & when
        ValidatableResponse response = 노선_삭제_요청(50);

        // then
        response.statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 삭제 시도")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> firstCreationRequest = 노선_생성_요청("신분당선", "bg-red-600");
        노선_생성_요청("1호선", "bg-red-600");
        long createdId = firstCreationRequest.jsonPath().getLong("id");

        // when
        ValidatableResponse response = 노선_삭제_요청(createdId);

        // then
        response.statusCode(HttpStatus.NO_CONTENT.value());
    }
}
