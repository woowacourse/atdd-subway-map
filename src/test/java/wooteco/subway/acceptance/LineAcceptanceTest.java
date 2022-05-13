package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
import wooteco.subway.dto.LineResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.utils.FixtureUtils.*;
import static wooteco.subway.utils.LineTestFixture.*;

@DisplayName("지하철 노선 E2E")
@Sql("/init.sql")
@SuppressWarnings("NonAsciiCharacters")
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
        ExtractableResponse<Response> 역_생성_응답_1 = post(STATION, Map.of("name", "지하철역"));
        long stationId1 = extractId(역_생성_응답_1);

        ExtractableResponse<Response> 역_생성_응답_2 = post(STATION, Map.of("name", "새로운지하철역"));
        long stationId2 = extractId(역_생성_응답_2);

        // when
        LinkedHashMap<String, Object> responseBody = post(LINE, 신분당선).jsonPath().get();
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
        ExtractableResponse<Response> response = get(LINE + "/" + 존재하지_않는_노선_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 목록 조회")
    @Test
    void showLines() {
        // given
        _7호선_및_역_생성요청();
        분당선_및_역_생성요청();

        // when
        ExtractableResponse<Response> response = get(LINE);

        List<Map<String, Object>> responseBody = response.jsonPath().get();
        List<Map<String, Object>> firstStations = (List<Map<String, Object>>) responseBody.get(0).get("stations");
        List<Map<String, Object>> secondStations = (List<Map<String, Object>>) responseBody.get(1).get("stations");


        // then
        assertAll(
                () -> assertThat(responseBody.get(0).get("id")).isNotNull(),
                () -> assertThat(responseBody.get(0).get("name")).isEqualTo("7호선"),
                () -> assertThat(responseBody.get(0).get("color")).isEqualTo("brown"),
                () -> assertAll(
                        () -> assertThat(firstStations.get(0).get("name")).isEqualTo("상도역"),
                        () -> assertThat(firstStations.get(1).get("name")).isEqualTo("이수역")
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
        ExtractableResponse<Response> response1 = post(LINE, 신분당선);
        long createdId = extractId(response1);

        // when & then
        ExtractableResponse<Response> response2 =
                put(LINE + "/" + createdId, Map.of("name", lineName, "color", "blue"));

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("색깔이 공백인 지하철 노선을 수정할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void canNotUpdateLineWithEmptyColor(String lineColor) {
        // given
        ExtractableResponse<Response> response = post(LINE, 신분당선);
        long createdId = extractId(response);

        // when & then
        ExtractableResponse<Response> response2 =
                put(LINE + "/" + createdId, Map.of("name", "신분당선", "color", lineColor));

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 생성_응답 = post(LINE, 신분당선);
        long createdId = extractId(생성_응답);

        // when
        ExtractableResponse<Response> 수정_응답 = put(LINE + "/" + createdId, _7호선);

        // then
        assertThat(수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value());

        ExtractableResponse<Response> response = get(LINE + "/" + createdId);
        LineResponse 생성된_노선 = convertObject(response, LineResponse.class);

        assertThat(extractId(생성_응답)).isEqualTo(createdId);
        assertThat(생성된_노선.getName()).isEqualTo(_7호선.get("name"));
        assertThat(생성된_노선.getColor()).isEqualTo(_7호선.get("color"));
    }

    @DisplayName("중복된 노선 이름 수정을 허용하지 않는다")
    @Test
    void canNotUpdateByDuplicationName() {
        // given
        ExtractableResponse<Response> 노선_생성_응답 = post(LINE, 신분당선);

        long createdId = extractId(노선_생성_응답);

        post(LINE, _7호선);

        // when
        ExtractableResponse<Response> response = put("/lines/" + createdId, _7호선);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 노선 수정 시도")
    @Test
    void updateNotExistLine() {
        // when & then
        long 존재하지_않는_노선_ID = 50L;
        ExtractableResponse<Response> response = put(LINE + "/" + 존재하지_않는_노선_ID, _7호선);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제 시도시 404 반환")
    @Test
    void deleteNotExistLine() {
        // given & when
        long 존재하지_않는_노선_ID = 50L;
        ExtractableResponse response = delete(LINE + "/" + 존재하지_않는_노선_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 삭제 시도")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> firstCreationRequest = post(LINE, 신분당선);
        post(LINE, 신분당선);
        long createdId = extractId(firstCreationRequest);

        // when
        ExtractableResponse response = delete("/lines/" + createdId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("구간을 등록할 수 있다")
    @Test
    void create_section() {
        노선_및_역_생성요청_케이스();

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
        노선_및_역_생성요청_케이스();

        // 구간 등록
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("upStationId", "1");
        requestBody.put("downStationId", "2");
        requestBody.put("distance", "3");

        post("/lines/1/sections", requestBody);

        // 구간 삭제 요청
        ExtractableResponse response = delete("/lines/1/sections?stationId=2");

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
