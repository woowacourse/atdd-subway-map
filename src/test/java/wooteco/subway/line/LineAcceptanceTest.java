package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.util.RestfulOrder.DEFAULT_MEDIA_TYPE;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;


public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        // when
        ExtractableResponse<Response> response = testResponse(params, DEFAULT_MEDIA_TYPE, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


    @DisplayName("중복된 노선 이름 추가시 예외 처리")
    @Test
    void nameDuplication() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        params.put("color", "bg-green-600");
        ExtractableResponse<Response> response = testResponse(params, DEFAULT_MEDIA_TYPE, "/lines");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "강남역");
        params2.put("color", "bg-green-600");
        ExtractableResponse<Response> createResponse2 = testResponse(params, DEFAULT_MEDIA_TYPE,
            "/lines");

        assertThat(createResponse2.statusCode())
            .isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        params1.put("color", "yellow darken-4");
        ExtractableResponse<Response> createResponse1 = testResponse(params1, DEFAULT_MEDIA_TYPE,
            "/lines");

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "서초역");
        params2.put("color", "yellow darken-4");
        ExtractableResponse<Response> createResponse2 = testResponse(params2, DEFAULT_MEDIA_TYPE,
            "/lines");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("하나의 지하철 노선을 상세 조회한다.")
    @Test
    void getLineDetail() {
        lineDao.save(new Line(1L, "name", "yellow darken-4"));
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(DEFAULT_MEDIA_TYPE)
            .when()
            .get("/lines/1")
            .then().log().all()
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("없는 지하철 노선 조회 시 예외 메시지를 출력한다")
    @Test
    void invalidLineId() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then().log().all()
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void modifyLine() {
        lineDao.save(new Line(1L, "name", "yellow darken-4"));

        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        params1.put("color", "bg-blue-600");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(params1)
            .when()
            .put("/lines/1")
            .then().log().all()
            .extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        params.put("color", "bg-green-600");
        ExtractableResponse<Response> createResponse = testResponse(params, DEFAULT_MEDIA_TYPE,
            "/lines");

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

    private void testRequest(Map<String, String> params, String mediaType, String path) {
        RestAssured.given().log().all()
            .body(params)
            .contentType(mediaType)
            .when()
            .post(path)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> testResponse(Map<String, String> params, String mediaType,
        String path) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(mediaType)
            .when()
            .post(path)
            .then().log().all()
            .extract();

        return response;
    }
}
