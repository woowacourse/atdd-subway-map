package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LINES_URI = "/lines";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "GREEN");
        // when
        ExtractableResponse<Response> response = postRequest(LINES_URI, params);
        // then
        assertAll(
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("GREEN"),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "BLUE");
        postRequest("/lines", params);
        // when
        ExtractableResponse<Response> response = postRequest(LINES_URI, params);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선들을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "4호선");
        params1.put("color", "SKYBLUE");

        ExtractableResponse<Response> createResponse1 = postRequest(LINES_URI, params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "5호선");
        params2.put("color", "PURPLE");
        ExtractableResponse<Response> createResponse2 = postRequest(LINES_URI, params2);
        // when
        ExtractableResponse<Response> response = getRequest(LINES_URI);
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

    @DisplayName("id를 통해 지하철 노선을 조회한다.")
    @Test
    void getLineById() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "6호선");
        params.put("color", "SKYBLUE");

        ExtractableResponse<Response> createdResponse = postRequest(LINES_URI, params);
        String uri = createdResponse.header("Location");
        // when
        ExtractableResponse<Response> response = getRequest(uri);
        // then
        assertAll(
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("6호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("SKYBLUE"),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "8호선");
        params.put("color", "PINK");
        ExtractableResponse<Response> createResponse = postRequest(LINES_URI, params);
        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        /// given
        Map<String, String> params = new HashMap<>();
        params.put("name", "13호선");
        params.put("color", "WHITE");

        ExtractableResponse<Response> createdResponse = postRequest(LINES_URI, params);
        String uri = createdResponse.header("Location");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "10호선");
        params2.put("color", "ORANGE");
        // when
        ExtractableResponse<Response> response = putRequest(uri, params2);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 ID의 노선을 수정한다.")
    @Test
    void updateLine_noExistLine_Exception() {
        // given
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "10호선");
        params2.put("color", "ORANGE");
        // when
        ExtractableResponse<Response> response = putRequest("/lines/10000", params2);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중복된 이름으로 노선을 수정한다.")
    @Test
    void updateLine_duplicateName_Exception() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "15호선");
        params.put("color", "BLUE");

        postRequest(LINES_URI, params);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "16호선");
        params2.put("color", "BLACK");

        ExtractableResponse<Response> createdResponse = postRequest(LINES_URI, params2);
        String uri = createdResponse.header("Location");

        Map<String, String> params3 = new HashMap<>();
        params3.put("name", "15호선");
        params3.put("color", "ORANGE");
        // when
        ExtractableResponse<Response> response = putRequest(uri, params3);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> postRequest(String path, Map<String, String> body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getRequest(String path) {
        return RestAssured.given().log().all()
                .when()
                .get(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> putRequest(String path, Map<String, String> body) {
        return RestAssured.given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .put(path)
                .then().log().all()
                .extract();
    }
}
