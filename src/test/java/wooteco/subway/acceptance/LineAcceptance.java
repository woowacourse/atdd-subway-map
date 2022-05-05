package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.line.LineResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptance extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "3호선");
        params.put("color", "테스트색4");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 생성시 예외가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "4호선");
        params.put("color", "테스트색3");
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void showLine() {
        /// given
        Map<String, String> params = new HashMap<>();
        params.put("name", "30호선");
        params.put("color", "녹색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String id = createResponse.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(id);
        assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("30호선");
        assertThat(response.body().jsonPath().get("color").toString()).isEqualTo("녹색");
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "55호선");
        params1.put("color", "테스트색1");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "56호선");
        params2.put("color", "테스트색2");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

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

    @DisplayName("노선을 업데이트한다.")
    @Test
    void updateLine() {
        /// given
        Map<String, String> originParams = new HashMap<>();
        originParams.put("name", "130호선");
        originParams.put("color", "테스트색11");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(originParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String id = createResponse.header("Location").split("/")[2];

        Map<String, String> newParams = new HashMap<>();
        newParams.put("name", "131호선");
        newParams.put("color", "테스트색12");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 업데이트에 실패한다.")
    @Test
    void failUpdateLine() {
        /// given
        Map<String, String> originParams = new HashMap<>();
        originParams.put("name", "120호선");
        originParams.put("color", "회색1");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(originParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String id = createResponse.header("Location").split("/")[2];

        originParams = new HashMap<>();
        originParams.put("name", "분당선");
        originParams.put("color", "yellow");
        RestAssured.given().log().all()
                .body(originParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> newParams = new HashMap<>();
        newParams.put("name", "분당선");
        newParams.put("color", "회색1");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선삭제")
    @Test
    void deleteLine() {
        /// given
        Map<String, String> originParams = new HashMap<>();
        originParams.put("name", "1994호선");
        originParams.put("color", "아이보리색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(originParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String id = createResponse.header("Location").split("/")[2];

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 노선 삭제시 예외 발생")
    @Test
    void invalidLine() {
        /// given
        var invalidId = 2002L;

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + invalidId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}

