package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("/lines에 대한 인수테스트")
class LineAcceptanceTest extends AcceptanceTest {


    private static final StationResponse STATION_RESPONSE_1 = new StationResponse(1L, "강남역");
    private static final StationResponse STATION_RESPONSE_2 = new StationResponse(2L, "양재역");
    private static final StationResponse STATION_RESPONSE_3 = new StationResponse(3L, "가천대역");
    private static final StationResponse STATION_RESPONSE_4 = new StationResponse(4L, "태평역");

    @DisplayName("POST /lines - 지하철 노선 생성 테스트")
    @Nested
    class CreateLineTest extends AcceptanceTest {

        @Test
        void 성공시_201_CREATED() {
            postStation(new HashMap<>() {{
                put("name", "강남역");
            }});
            postStation(new HashMap<>() {{
                put("name", "양재역");
            }});

            HashMap<String, Object> lineParams = makeLineJson("신분당선", "빨간색", 1L, 2L, 10);

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

            LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);
            List<StationResponse> stations = List.of(new StationResponse(1L, "강남역"),
                new StationResponse(2L, "양재역"));
            LineResponse expected = new LineResponse(1L, "신분당선", "빨간색", stations);
            assertAll(() -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                assertThat(response.header("Location")).isNotBlank();
                assertThat(actual).isEqualTo(expected);
            });
        }

        @Test
        void 이미_존재하는_노선명_입력시_400_BAD_REQUEST() {
            HashMap<String, Object> params = makeLineJson("신분당선", "빨간색", 1L, 2L, 10);
            postLine(params);

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("GET /lines - 지하철 노선 목록 조회 테스트")
    @Test
    void 성공시_200_OK() {
        postStation(makeStationJson("강남역"));
        postStation(makeStationJson("양재역"));
        postStation(makeStationJson("가천대역"));
        postStation(makeStationJson("태평역"));

        HashMap<String, Object> lineParams1 = makeLineJson("신분당선", "빨간색", 1L, 2L, 10);
        HashMap<String, Object> lineParams2 = makeLineJson("분당선", "노란색", 3L, 4L, 10);
        postLine(lineParams1);
        postLine(lineParams2);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        List<LineResponse> responseBody = response.jsonPath()
            .getList(".", LineResponse.class);
        List<LineResponse> expected = List.of(
            new LineResponse(1L, "신분당선", "빨간색", List.of(STATION_RESPONSE_1, STATION_RESPONSE_2)),
            new LineResponse(2L, "분당선", "노란색", List.of(STATION_RESPONSE_3, STATION_RESPONSE_4))
        );

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(responseBody).hasSize(2);
            assertThat(responseBody).isEqualTo(expected);
        });
    }

    @DisplayName("GET /lines/:id - 지하철 노선 조회 테스트")
    @Nested
    class ShowLineTest extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

            LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);
            LineResponse expected = new LineResponse(1L, "신분당선", "빨간색",
                List.of(STATION_RESPONSE_1, STATION_RESPONSE_2));
            assertAll(() -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                assertThat(actual).isEqualTo(expected);
            });
        }

        @Test
        void 존재하지_않는_노선인_경우_404_NOT_FOUND() {
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @DisplayName("PUT /lines/:id - 지하철 노선 수정 테스트")
    @Nested
    class UpdateLineTest extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, String> params = new HashMap<>() {{
                put("name", "NEW 분당선");
                put("color", "빨간색");
            }};

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 수정하려는_지하철_노선이_존재하지_않는_경우_400_BAD_REQUEST() {
            HashMap<String, String> params = new HashMap<>() {{
                put("name", "NEW 분당선");
                put("color", "빨간색");
            }};

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 이미_존재하는_지하철_노선_이름으로_수정시_400_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));
            postLine(makeLineJson("NEW_분당선", "빨간색", 1L, 2L, 10));
            Map<String, String> params = new HashMap<>() {{
                put("name", "NEW_분당선");
                put("color", "빨간색");
            }};

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("DELETE /lines/:id - 지하철 노선 제거 테스트")
    @Nested
    class DeleteLineTest extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/1")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void 삭제하려는_지하철_노선이_존재하지_않는_경우_BAD_REQUEST() {
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/1")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("POST /lines/:id/sections - 지하철 노선 구간 추가 테스트")
    @Nested
    class AddStationToLine extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postStation(makeStationJson("양재시민의숲역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, Object> sectionParams = makeSectionJson(2L, 3L, 4);

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 존재하지_않는_지하철역을_입력할_경우_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, Object> sectionParams = makeSectionJson(2L, 3L, 4);

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 상행역_하행역_둘다_노선에_등록된_경우_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, Object> sectionParams = makeSectionJson(1L, 2L, 5);

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 상행역_하행역_둘다_노선에_등록되지_않은_경우_BAD_REQUEST() {
            postStation(makeStationJson("강남역"));
            postStation(makeStationJson("양재역"));
            postLine(makeLineJson("신분당선", "빨간색", 1L, 2L, 10));

            HashMap<String, Object> sectionParams = makeSectionJson(3L, 4L, 5);
        }
    }

    @DisplayName("POST /lines/:id/sections - 지하철 노선 구간 추가 테스트")
    @Nested
    class RemoveStationToLine extends AcceptanceTest {
    }

    private HashMap<String, Object> makeLineJson(String name, String color, Long upStationId,
        Long downStationId, int distance) {
        return new HashMap<>() {{
            put("name", name);
            put("color", color);
            put("upStationId", upStationId);
            put("downStationId", downStationId);
            put("distance", distance);
        }};
    }

    private HashMap<String, String> makeStationJson(String name) {
        return new HashMap<>() {{
            put("name", name);
        }};
    }

    private HashMap<String, Object> makeSectionJson(Long upStationId, Long downStationId,
        int distance) {
        return new HashMap<>() {{
            put("upStationId", upStationId);
            put("downStationId", downStationId);
            put("distance", distance);
        }};
    }

    private void postLine(Map<String, Object> params) {
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private void postStation(Map<String, String> params) {
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }
}
