package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.test_utils.HttpMethod;
import wooteco.subway.test_utils.HttpUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("인수테스트 - /lines")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("POST /lines - 지하철 노선 생성 테스트")
    @Nested
    class CreateLineTest extends AcceptanceTest {

        @Test
        void 성공시_201_CREATED() {
            Map<String, String> params = jsonLineOf("신분당선", "bg-red-600");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/lines", params);
            LineResponse actualBody = response.jsonPath().getObject(".", LineResponse.class);
            LineResponse expectedBody = new LineResponse(1L, "신분당선", "bg-red-600");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.header("Location")).isNotBlank();
            assertThat(actualBody).isEqualTo(expectedBody);
        }

        @Test
        void 이미_존재하는_노선명_입력시_400_BAD_REQUEST() {
            Map<String, String> params = jsonLineOf("신분당선", "bg-red-600");
            postLine(params);

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.POST, "/lines", params);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("GET /lines - 지하철 노선 목록 조회 테스트")
    @Test
    void 성공시_200_OK() {
        postLine("신분당선", "bg-red-600");
        postLine("분당선", "bg-green-600");

        ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.GET, "/lines");
        List<LineResponse> responseBody = response.jsonPath().getList(".", LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(responseBody).hasSize(2);
    }

    @DisplayName("GET /lines/:id - 지하철 노선 조회 테스트")
    @Nested
    class ShowLineTest extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postLine("신분당선", "bg-red-600");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.GET, "/lines/1");
            LineResponse actualBody = response.jsonPath().getObject(".", LineResponse.class);
            LineResponse expectedBody = new LineResponse(1L, "신분당선", "bg-red-600");

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
    class UpdateLineTest extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postLine("신분당선", "bg-red-600");
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
            Map<String, String> duplicateLineParams = jsonLineOf("NEW 분당선", "bg-red-600");
            postLine(duplicateLineParams);
            postLine("신분당선", "bg-red-600");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.PUT, "/lines/2", duplicateLineParams);

            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }
    }

    @DisplayName("DELETE /lines/:id - 지하철 노선 제거 테스트")
    @Nested
    class DeleteLineTest extends AcceptanceTest {

        @Test
        void 성공시_204_OK() {
            postLine("신분당선", "bg-red-600");

            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/lines/1");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void 삭제하려는_지하철_노선이_존재하지_않는_경우_404_NOT_FOUND() {
            ExtractableResponse<Response> response = HttpUtils.send(HttpMethod.DELETE, "/lines/99999");

            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
    }

    private HashMap<String, String> jsonLineOf(String name, String color) {
        return new HashMap<>() {{
            put("name", name);
            put("color", color);
        }};
    }

    private void postLine(String name, String color) {
        postLine(jsonLineOf(name, color));
    }

    private void postLine(Map<String, String> params) {
        HttpUtils.send(HttpMethod.POST, "/lines", params);
    }
}
