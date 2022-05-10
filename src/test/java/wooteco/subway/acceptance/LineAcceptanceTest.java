package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.dto.response.StationResponse;
import wooteco.subway.test_utils.HttpMethod;
import wooteco.subway.test_utils.HttpUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("인수테스트 - /lines")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final StationResponse STATION_1 = new StationResponse(1L, "강남역");
    private static final StationResponse STATION_2 = new StationResponse(2L, "선릉역");
    private static final StationResponse STATION_3 = new StationResponse(3L, "잠실역");
    private static final List<StationResponse> STATION_RESPONSE_1_2 = List.of(STATION_1, STATION_2);
    private static final List<StationResponse> STATION_RESPONSE_1_2_3 = List.of(STATION_1, STATION_2, STATION_3);
    private static final List<StationResponse> STATION_RESPONSE_1_3 = List.of(STATION_1, STATION_3);
    private static final List<StationResponse> STATION_RESPONSE_3_1 = List.of(STATION_3, STATION_1);

    @BeforeAll
    void setup() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("setup_test_db.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("lines_assurance_test_fixture.sql"));
        }
    }

    @AfterEach
    public void cleanseAndSetup() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("lines_assurance_test_fixture.sql"));
        }
    }

    @DisplayName("POST /lines - 지하철 노선 생성 테스트")
    @Nested
    class CreateLineTest {

        @Test
        void 성공시_201_CREATED() {
            Map<String, Object> params = jsonLineOf("신분당선", "bg-red-600", 1L, 2L, 10);

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/lines", params);
            LineResponse actualBody = extractSingleLineResponseBody(response);
            LineResponse expectedBody = new LineResponse(4L, "신분당선", "bg-red-600", STATION_RESPONSE_1_2);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
            assertThat(actualBody).isEqualTo(expectedBody);
        }

        @Test
        void 정보가_담기지_않은_경우_400_BAD_REQUEST() {
            Map<String, String> emptyParams = new HashMap<>();

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", emptyParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 정보가_공백으로_구성된_경우_400_BAD_REQUEST() {
            Map<String, Object> blankParams = jsonLineOf("   ", "  ", 1L, 2L, 10);

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", blankParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 이미_존재하는_노선명_입력시_400_BAD_REQUEST() {
            Map<String, Object> params = jsonLineOf("존재하는 노선명", "bg-red-600", 1L, 2L, 10);

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/lines", params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 존재하지_않는_지하철역을_종점으로_입력하면_404_NOT_FOUND() {
            Map<String, Object> params = jsonLineOf("새로운 이름", "검정색", 9999L, 2L, 10);

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/lines", params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @DisplayName("GET /lines - 지하철 노선 목록 조회 테스트")
    @Nested
    class ShowStationsTest {

        @Test
        void 성공시_200_OK() {
            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.GET, "/lines");
            List<LineResponse> actualBody = extractJsonBody(response);
            List<LineResponse> expectedBody = List.of(
                    new LineResponse(1L, "1호선", "노란색", STATION_RESPONSE_1_3),
                    new LineResponse(2L, "2호선", "빨간색", STATION_RESPONSE_1_2_3),
                    new LineResponse(3L, "존재하는 노선명", "초록색", STATION_RESPONSE_1_3)
            );

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(actualBody).isEqualTo(expectedBody);
        }

        private List<LineResponse> extractJsonBody(ExtractableResponse<Response> response) {
            return response.jsonPath().getList(".", LineResponse.class);
        }
    }

    @DisplayName("GET /lines/:id - 지하철 노선 조회 테스트")
    @Nested
    class ShowLineTest {

        // TODO: 순서대로 정렬하여 조회
        @Test
        void 성공시_200_OK() {
            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.GET, "/lines/1");
            LineResponse actualBody = extractSingleLineResponseBody(response);
            LineResponse expectedBody = new LineResponse(1L, "1호선", "노란색", STATION_RESPONSE_3_1);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(actualBody).isEqualTo(expectedBody);
        }

        @Test
        void 존재하지_않는_노선인_경우_404_NOT_FOUND() {
            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.GET, "/lines/99999");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    @DisplayName("PUT /lines/:id - 지하철 노선 수정 테스트")
    @Nested
    class UpdateLineTest {

        @Test
        void 성공시_200_OK() {
            Map<String, String> params = jsonLineOf("NEW 분당선", "bg-red-800");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.PUT, "/lines/1", params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 수정하려는_지하철_노선이_존재하지_않는_경우_404_NOT_FOUND() {
            Map<String, String> params = jsonLineOf("NEW 분당선", "bg-red-600");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.PUT, "/lines/9999", params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void 이미_존재하는_지하철_노선_이름으로_수정시_400_BAD_REQUEST() {
            Map<String, String> duplicateNameParams = jsonLineOf("존재하는 노선명", "bg-red-600");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.PUT, "/lines/2", duplicateNameParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 이름_혹은_색상_정보가_담기지_않은_경우_400_BAD_REQUEST() {
            Map<String, String> emptyParams = new HashMap<>();

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", emptyParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 이름_혹은_색상_정보가_공백으로_구성된_경우_400_BAD_REQUEST() {
            Map<String, String> blankParams = jsonLineOf("   ", "  ");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/stations", blankParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("DELETE /lines/:id - 지하철 노선 제거 테스트")
    @Nested
    class DeleteLineTest {

        @Test
        void 성공시_204_OK() {
            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/lines/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void 삭제하려는_지하철_노선이_존재하지_않는_경우_404_NOT_FOUND() {
            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/lines/99999");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    private HashMap<String, String> jsonLineOf(String name,
                                               String color) {
        return new HashMap<>() {{
            put("name", name);
            put("color", color);
        }};
    }

    private HashMap<String, Object> jsonLineOf(String name,
                                               String color,
                                               Long upStationId,
                                               Long downStationId,
                                               int distance) {
        return new HashMap<>() {{
            put("name", name);
            put("color", color);
            put("upStationId", upStationId);
            put("downStationId", downStationId);
            put("distance", distance);
        }};
    }

    private LineResponse extractSingleLineResponseBody(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class);
    }
}
