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
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.DbLineDao;
import wooteco.subway.dao.DbStationDao;
import wooteco.subway.dao.MemorySectionDao;
import wooteco.subway.domain.Section;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.utils.LineAcceptanceTestFixture.*;
import static wooteco.subway.acceptance.utils.StationAcceptanceTestFixture.역_생성_요청;

@DisplayName("지하철 노선 E2E")
@Sql("/init.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private DbStationDao stationDao;
    @Autowired
    private DbLineDao lineDao;

    @Autowired
    private MemorySectionDao sectionDao;

    @BeforeEach
    void beforeEach() {
        stationDao.deleteAll();
        lineDao.deleteAll();
        sectionDao.deleteAll();
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

    @DisplayName("노선 정상 등록")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> createStationResponse1 = 역_생성_요청("지하철역");
        long stationId1 = createStationResponse1.jsonPath().getLong("id");

        ExtractableResponse<Response> createStationResponse2 = 역_생성_요청("새로운지하철역");
        long stationId2 = createStationResponse2.jsonPath().getLong("id");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "신분당선");
        requestBody.put("color", "yellow");
        requestBody.put("upStationId", String.valueOf(stationId1));
        requestBody.put("downStationId", String.valueOf(stationId2));
        requestBody.put("distance", "10");

        // when
        ValidatableResponse response = 노선_생성_요청(requestBody);
        LinkedHashMap<String, Object> responseBody = response.extract().jsonPath().get();
        List<Map<String, String>> stationsResponse = (List<Map<String, String>>) responseBody.get("stations");

        // then
        assertAll(
                () -> assertThat(responseBody.get("id")).isNotNull(),
                () -> assertThat(responseBody.get("name")).isEqualTo("신분당선"),
                () -> assertThat(responseBody.get("color")).isEqualTo("yellow"),
                () -> assertAll(
                        () -> assertThat(Integer.valueOf(String.valueOf(stationsResponse.get(0).get("id"))))
                                .isEqualTo(Integer.valueOf(String.valueOf(stationId1))),
                        () -> assertThat(stationsResponse.get(0).get("name")).isEqualTo("지하철역"),
                        () -> assertThat(Integer.valueOf(String.valueOf(stationsResponse.get(1).get("id"))))
                                .isEqualTo(Integer.valueOf(String.valueOf(stationId2))),
                        () -> assertThat(stationsResponse.get(1).get("name")).isEqualTo("새로운지하철역")
                )
        );
    }

    @DisplayName("지하철 노선 중복 등록을 허용하지 않는다")
    @Test
    void createStationWithDuplicateName() {
        // given
        String name = "신분당선";
        String color = "bg-red-600";
        노선_생성_요청(name, color);

        ExtractableResponse<Response> response = 노선_생성_요청(name, color);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 노선 조회시 예외를 반환한다")
    @Test
    void showNotExistLine() {
        // when
        ExtractableResponse<Response> response = 노선_조회_요청(50L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 목록 조회")
    @Test
    void showLines() {
        // given
        노선_및_역들_생성요청_케이스_1번();
        노선_및_역들_생성요청_케이스_2번();

        // when
        ExtractableResponse<Response> response = 노선_목록_조회_요청();

        List<Map<String, Object>> responseBody = response.jsonPath().get();
        List<Map<String, Object>> firstStations = (List<Map<String, Object>>) responseBody.get(0).get("stations");
        List<Map<String, Object>> secondStations = (List<Map<String, Object>>) responseBody.get(1).get("stations");

        // then
        assertAll(
                () -> assertThat(responseBody.get(0).get("id")).isNotNull(),
                () -> assertThat(responseBody.get(0).get("name")).isEqualTo("1호선"),
                () -> assertThat(responseBody.get(0).get("color")).isEqualTo("blue"),
                () -> assertAll(
                        () -> assertThat(firstStations.get(0).get("name")).isEqualTo("노량진역"),
                        () -> assertThat(firstStations.get(1).get("name")).isEqualTo("영등포역")
                ),
                () -> assertThat(responseBody.get(1).get("id")).isNotNull(),
                () -> assertThat(responseBody.get(1).get("name")).isEqualTo("신분당선"),
                () -> assertThat(responseBody.get(1).get("color")).isEqualTo("yellow"),
                () -> assertAll(
                        () -> assertThat(secondStations.get(0).get("name")).isEqualTo("강남구청역"),
                        () -> assertThat(secondStations.get(1).get("name")).isEqualTo("선릉역")
                )
        );
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

    @DisplayName("구간을 등록할 수 있다")
    @Test
    void create_section() {
        노선_및_역들_생성요청_케이스_3번();

        // 구간 등록 요청
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("upStationId", "1");
        requestBody.put("downStationId", "2");
        requestBody.put("distance", "3");

        ValidatableResponse response = RestAssured.given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all();

        // then
        response.statusCode(HttpStatus.OK.value());

        Section firstSection = sectionDao.findById(1L).get();
        Section secondSection = sectionDao.findById(2L).get();

        // 1번역 - (거리 3) - 2번역 - (거리 4) - 3번역
        assertThat(firstSection).isEqualTo(new Section(1L, 2L, 3L, 4, 1L));
        assertThat(secondSection).isEqualTo(new Section(2L, 1L, 2L, 3, 1L));
    }

    @DisplayName("구간을 삭제할 수 있다")
    @Test
    void delete_section() {
        // 최조 등록
        노선_및_역들_생성요청_케이스_3번();

        // 구간 등록
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("upStationId", "1");
        requestBody.put("downStationId", "2");
        requestBody.put("distance", "3");
        RestAssured.given().log().all()
                .body(requestBody)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all();

        // 구간 삭제 요청
        ValidatableResponse response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/1/sections?stationId=2")
                .then().log().all();

        response.statusCode(HttpStatus.OK.value());
        Section deletedSection = sectionDao.findById(1L).get();

        // 1번역 - (거리 3) - 2번역 - (거리 4) - 3번역
        // 1번역 - (거리 7) - 3번역
        assertThat(deletedSection).isEqualTo(new Section(1L, 1L, 3L, 7, 1L));
    }
}
