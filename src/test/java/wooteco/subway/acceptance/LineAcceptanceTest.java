package wooteco.subway.acceptance;

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
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.DbLineDao;
import wooteco.subway.dao.DbStationDao;
import wooteco.subway.dao.MemorySectionDao;
import wooteco.subway.domain.Section;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.EMPTY_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.utils.Fixture.*;
import static wooteco.subway.utils.LineTestFixture.*;

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
        ExtractableResponse<Response> response = post(LINE, requestBody);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
        ExtractableResponse<Response> response = post(LINE, requestBody);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 정상 등록")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> createStationResponse1 =
                post(STATION, Map.of("name", "지하철역"));
        long stationId1 = createStationResponse1.jsonPath().getLong("id");

        ExtractableResponse<Response> createStationResponse2
                = post(STATION, Map.of("name", "새로운지하철역"));
        long stationId2 = createStationResponse2.jsonPath().getLong("id");

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "신분당선");
        requestBody.put("color", "yellow");
        requestBody.put("upStationId", String.valueOf(stationId1));
        requestBody.put("downStationId", String.valueOf(stationId2));
        requestBody.put("distance", "10");

        // when
        LinkedHashMap<String, Object> responseBody = post(LINE, requestBody).jsonPath().get();
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
        post(LINE, 신분당선);

        // when & then
        ExtractableResponse<Response> response = post(LINE, 신분당선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 노선 조회시 예외를 반환한다")
    @Test
    void showNotExistLine() {
        // given
        long 존재하지_않는_노선_ID = 50L;

        // when
        ExtractableResponse<Response> response = get("/lines/" + 존재하지_않는_노선_ID, Map.of());

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
//        ExtractableResponse<Response> response = 노선_등록_요청("신분당선", "bg-red-600");
        ExtractableResponse<Response> response = post(LINE, 신분당선);
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
//        ExtractableResponse<Response> response = 노선_등록_요청("신분당선", "bg-red-600");
        ExtractableResponse<Response> response = post(LINE, 신분당선);
        long createdId = response.jsonPath().getLong("id");

        // when & then
        노선_수정_요청(createdId, "신분당선", lineColor)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = post(LINE, 신분당선);
        long createdId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> updateResponse = put("/lines/" + createdId, _1호선);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response = get("/lines/" + createdId, EMPTY_MAP);

        assertThat(response.jsonPath().getLong("id")).isEqualTo(createdId);
        assertThat(response.jsonPath().getString("name")).isEqualTo(_1호선.get("name"));
        assertThat(response.jsonPath().getString("color")).isEqualTo(_1호선.get("color"));
    }

    @DisplayName("중복된 노선 이름 수정을 허용하지 않는다")
    @Test
    void canNotUpdateByDuplicationName() {
        // given
        ExtractableResponse<Response> firstResponse = post(LINE, 신분당선);

        long createdId = firstResponse.jsonPath().getLong("id");

        post(LINE, _1호선);

        // when
        ExtractableResponse<Response> response = put("/lines/" + createdId, _1호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 노선 수정 시도")
    @Test
    void updateNotExistLine() {
        // when & then
        put("/lines/50", _1호선);
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제 시도시 404 반환")
    @Test
    void deleteNotExistLine() {
        // given & when
        long 존재하지_않는_노선_ID = 50L;
        delete("/delete" + 존재하지_않는_노선_ID, Map.of());
        ValidatableResponse response = 노선_삭제_요청(50);

        // then
        response.statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 삭제 시도")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> firstCreationRequest = post(LINE, 신분당선);
//        노선_등록_요청("1호선", "bg-red-600");
        post(LINE, 신분당선);
        long createdId = firstCreationRequest.jsonPath().getLong("id");

        // when
//        ValidatableResponse response = 노선_삭제_요청(createdId);
        ExtractableResponse response = delete("/lines/" + createdId, EMPTY_MAP);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
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

        ExtractableResponse<Response> response = post("/lines/1/sections", requestBody);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        Section firstSection = sectionDao.findById(1L).get();
        Section secondSection = sectionDao.findById(2L).get();

        // 1번역 - (거리 3) - 2번역 - (거리 4) - 3번역
        assertThat(firstSection).isEqualTo(new Section(1L, 2L, 3L, 4, 1L));
        assertThat(secondSection).isEqualTo(new Section(2L, 1L, 2L, 3, 1L));
    }

    @DisplayName("구간을 삭제할 수 있다")
    @Test
    void delete_section() {
        // given
        // 최조 등록
        노선_및_역들_생성요청_케이스_3번();

        // 구간 등록
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("upStationId", "1");
        requestBody.put("downStationId", "2");
        requestBody.put("distance", "3");

        post("/lines/1/sections", requestBody);

        // 구간 삭제 요청
        ExtractableResponse response = delete("/lines/1/sections?stationId=2", EMPTY_MAP);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Section deletedSection = sectionDao.findById(1L).get();

        // then
        /**
         * 1번역 - (거리 3) - 2번역 - (거리 4) - 3번역
         * - 2번역 삭제 후 -
         * 1번역 - (거리 7) - 3번역
         */
        assertThat(deletedSection).isEqualTo(new Section(1L, 1L, 3L, 7, 1L));
    }
}
