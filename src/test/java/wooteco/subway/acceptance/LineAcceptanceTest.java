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
