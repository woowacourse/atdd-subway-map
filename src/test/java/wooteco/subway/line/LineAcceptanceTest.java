package wooteco.subway.line;


import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.domain.line.Line;
import wooteco.subway.web.dto.LineResponse;

@DisplayName("노선 인수 테스트")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final Map<String, String> params1 = new HashMap<>();
    private static final Map<String, String> params2 = new HashMap<>();

    static {
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");
        params2.put("name", "2호선");
        params2.put("color", "bg-green-600");
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    @Test
    @DisplayName("중복된 지하철 노선을 생성할 수 없다.")
    void cannotCreateDuplicatedLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선을 조회한다.")
    void showLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> listResponse = getLineListResponse();

        // then
        assertThat(listResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Line> expectedLines = Stream.of(createResponse1, createResponse2)
                .map(response -> new Line(
                        response.jsonPath().getLong("id"),
                        response.jsonPath().getString("name"),
                        response.jsonPath().getString("color")
                ))
                .collect(Collectors.toList());

        List<Line> resultLines = listResponse.jsonPath().getList(".").stream()
                .map(obj -> {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    return new Line(
                            Long.valueOf((Integer) map.get("id")),
                            (String) map.get("name"),
                            (String) map.get("color")
                    );
                })
                .collect(Collectors.toList());

        for (int i = 0; i < resultLines.size(); i++) {
            Line result = resultLines.get(i);
            Line expected = expectedLines.get(i);
            assertThat(result.getId()).isEqualTo(expected.getId());
            assertThat(result.getName()).isEqualTo(expected.getName());
            assertThat(result.getColor()).isEqualTo(expected.getColor());
        }

        assertThat(resultLines).containsAll(expectedLines);
    }

    @Test
    @DisplayName("지하철 노선을 제거한다.")
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> lineListResponse = getLineListResponse();
        assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<LineResponse> lineResponses = lineListResponse.jsonPath().getList(".", LineResponse.class);
        assertThat(lineResponses.size()).isEqualTo(1);
        assertThat(lineResponses.get(0).getName()).isEqualTo(params2.get("name"));
        assertThat(lineResponses.get(0).getColor()).isEqualTo(params2.get("color"));
    }

    private ExtractableResponse<Response> getLineListResponse() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }
}
