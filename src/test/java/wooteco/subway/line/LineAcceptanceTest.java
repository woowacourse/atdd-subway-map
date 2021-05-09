package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선 생성 - 성공")
    @Test
    void createLine() {
        // given
        Map<String, String> lineInfo = 노선_정보("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = 노선_생성(lineInfo);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("노선 생성 - 실패(이름 중복)")
    @Test
    void createLine_duplicatedName() {
        // given
        Map<String, String> params = 노선_정보("신분당선", "bg-red-600");
        노선_생성(params);

        // when
        ExtractableResponse<Response> response = 노선_생성(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.header("Location")).isBlank();
    }

    @DisplayName("노선 목록 조회 - 성공")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = 노선_정보("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse1 = 노선_생성(params1);

        Map<String, String> params2 = 노선_정보("2호선", "bg-green-600");
        final ExtractableResponse<Response> createResponse2 = 노선_생성(params2);

        // when
        ExtractableResponse<Response> response = 한_노선_조회("/lines");

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

    @DisplayName("한 노선 조회 - 성공")
    @Test
    void getLineById() {
        /// given
        Map<String, String> params = 노선_정보("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse = 노선_생성(params);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = 한_노선_조회(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }

    @DisplayName("노선 조회 - 실패(노선 정보 없음)")
    @Test
    void getStationById_notFound() {
        /// given
        ExtractableResponse<Response> response = 한_노선_조회("/lines/-1");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 수정 - 성공")
    @Test
    void updateLine() {
        /// given
        Map<String, String> params = 노선_정보("신분당선", "bg-red-600");
        final String uri = 노선_생성(params).header("Location");
        Map<String, String> params1 = 노선_정보("구분당선", "bg-blue-600");

        // when
        ExtractableResponse<Response> result = 노선_수정(params1, uri);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선 수정 - 실패(변경하려는 노선 이름 중복)")
    @Test
    void updateLine_duplicatedName() {
        /// given
        Map<String, String> params = 노선_정보("신분당선", "bg-red-600");
        final String uri = 노선_생성(params).header("Location");
        Map<String, String> params1 = 노선_정보("구분당선", "bg-red-600");
        노선_생성(params1);
        Map<String, String> params2 = 노선_정보("구분당선", "bg-blue-600");

        // when
        ExtractableResponse<Response> result = 노선_수정(params2, uri);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.body().asString()).isEqualTo("이미 등록되어 있는 노선 이름입니다.");
    }

    @DisplayName("노선 수정 - 실패(존재 하지 않는 노선 수정)")
    @Test
    void updateLine_notFound() {
        /// given
        Map<String, String> params = 노선_정보("구분당선", "bg-blue-600");

        // when
        ExtractableResponse<Response> result = 노선_수정(params, "/lines/-1");

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 삭제 - 성공")
    @Test
    void removeLine() {
        /// given
        Map<String, String> params = 노선_정보("신분당선", "bg-red-600");
        final String uri = 노선_생성(params).header("Location");

        // when
        ExtractableResponse<Response> result = 노선_삭제(uri);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 노선_삭제(String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_수정(Map<String, String> params1, String uri) {
        return RestAssured.given().log().all()
                .body(params1)
                .contentType(ContentType.JSON)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 한_노선_조회(String url) {
        return RestAssured.given().log().all()
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_생성(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private Map<String, String> 노선_정보(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        return params;
    }
}