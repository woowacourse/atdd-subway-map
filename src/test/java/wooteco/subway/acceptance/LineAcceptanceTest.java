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
import wooteco.subway.dto.response.LineResponse;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("인수테스트 - /lines")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("POST /lines - 지하철 노선 생성 테스트")
    @Nested
    class CreateLineTest extends AcceptanceTest {

        @Test
        void 성공시_201_CREATED() {
            Map<String, String> params = new HashMap<>() {{
                put("name", "신분당선");
                put("color", "bg-red-600");
            }};

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .post("/lines")
                    .then().log().all()
                    .extract();

            LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);
            LineResponse expected = new LineResponse(1L, "신분당선", "bg-red-600");
            assertAll(() -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                assertThat(response.header("Location")).isNotBlank();
                assertThat(actual).isEqualTo(expected);
            });
        }

        @Test
        void 이미_존재하는_노선명_입력시_400_BAD_REQUEST() {
            Map<String, String> params = new HashMap<>() {{
                put("name", "신분당선");
                put("color", "bg-red-600");
            }};
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
        postLine("신분당선", "bg-red-600");
        postLine("분당선", "bg-green-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        List<LineResponse> responseBody = response.jsonPath().getList(".", LineResponse.class);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(responseBody).hasSize(2);
        });
    }

    @DisplayName("GET /lines/:id - 지하철 노선 조회 테스트")
    @Nested
    class ShowLineTest extends AcceptanceTest {

        @Test
        void 성공시_200_OK() {
            postLine("신분당선", "bg-red-600");

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .when()
                    .get("/lines/1")
                    .then().log().all()
                    .extract();

            LineResponse actual = response.jsonPath().getObject(".", LineResponse.class);
            LineResponse expected = new LineResponse(1L, "신분당선", "bg-red-600");
            assertAll(() -> {
                assertThat(actual).isEqualTo(expected);
                assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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
            postLine("신분당선", "bg-red-600");
            Map<String, String> params = new HashMap<>() {{
                put("name", "NEW 분당선");
                put("color", "bg-red-800");
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
            Map<String, String> params = new HashMap<>() {{
                put("name", "NEW 분당선");
                put("color", "bg-red-600");
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
            Map<String, String> existingLineInfo = new HashMap<>() {{
                put("name", "NEW_분당선");
                put("color", "bg-red-600");
            }};
            postLine(existingLineInfo);
            postLine("신분당선", "bg-red-600");

            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .body(existingLineInfo)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .put("/lines/2")
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
            postLine("신분당선", "bg-red-600");

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

    private void postLine(String name, String color) {
        postLine(new HashMap<>() {{
            put("name", name);
            put("color", color);
        }});
    }

    private void postLine(Map<String, String> params) {
        RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines");
    }
}
