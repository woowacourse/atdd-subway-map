package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        // when
        ExtractableResponse<Response> response = postLine(params);
        LineResponse lineResponse = response.as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(lineResponse.getId()).isNotNull();
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    private ExtractableResponse<Response> postLine(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "신분당선");
        ExtractableResponse<Response> response = postLine(params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선 색으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-red-600");
        params2.put("name", "분당선");
        ExtractableResponse<Response> response = postLine(params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> createResponse1 = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        ExtractableResponse<Response> createResponse2 = postLine(params2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("특정 ID의 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        postLine(params2);

        // when
        ExtractableResponse<Response> response = getLine(oldResponse.header("Location"));
        LineResponse lineResponse = response.as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getId()).isEqualTo(Long.parseLong(oldResponse.header("Location").split("/")[2]));
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
    }

    private ExtractableResponse<Response> getLine(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    @DisplayName("존재하지 않는 id의 노선을 조회한다.")
    @Test
    void getLineOfIdDoesNotExist() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        postLine(params2);

        // when
        ExtractableResponse<Response> response = getLine("/lines/0");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        ExtractableResponse<Response> response = putLine(oldResponse.header("Location"), params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 id의 노선을 수정한다.")
    @Test
    void updateLineOfIdDoesNotExist() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        postLine(params1);

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        ExtractableResponse<Response> response = putLine("/lines/0", params2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 존재하는 이름으로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        postLine(params2);

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("color", "bg-green-600");
        params3.put("name", "분당선");
        ExtractableResponse<Response> response = putLine(oldResponse.header("Location"), params3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 존재하는 색으로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateColor() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-blue-600");
        params2.put("name", "분당선");
        postLine(params2);

        // when
        Map<String, String> params3 = new HashMap<>();
        params3.put("color", "bg-blue-600");
        params3.put("name", "2호선");
        ExtractableResponse<Response> response = putLine(oldResponse.header("Location"), params3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> putLine(String path, Map<String, String> params3) {
        return RestAssured.given().log().all()
                .when()
                .body(params3)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put(path)
                .then().log().all()
                .extract();
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        ExtractableResponse<Response> oldResponse = postLine(params1);

        // when
        ExtractableResponse<Response> response = deleteLine(oldResponse.header("Location"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> deleteLine(String path) {
        return RestAssured.given().log().all()
                .when()
                .delete(path)
                .then()
                .log().all()
                .extract();
    }

    @DisplayName("id가 존재하지 않는 노선을 삭제한다.")
    @Test
    void deleteLineOfIdDoesNotExist() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        postLine(params1);

        // when
        ExtractableResponse<Response> response = deleteLine("/lines/0");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
