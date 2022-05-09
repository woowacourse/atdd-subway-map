package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @ParameterizedTest(name = "{displayName} : {arguments}")
    @ValueSource(strings = {"a", "1234567890"})
    @DisplayName("지하철 노선 이름의 길이를 1 이상 10 이하로 생성할 수 있다.")
    void createLineWithValidName(String name) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "초록색");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @ParameterizedTest(name = "{displayName} : {arguments}")
    @ValueSource(strings = {"", "12345678901"})
    @DisplayName("지하철 노선 이름의 길이를 1 이상 10 이하가 아니면 생성할 수 없다.")
    void createLineWithNonValidName(String name) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", "초록색");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("노선 이름의 길이는 1 이상 10 이하여야 합니다.")
        );
    }

    @ParameterizedTest(name = "{displayName} : {arguments}")
    @ValueSource(strings = {"", "123456789012345678901"})
    @DisplayName("지하철 노선 색의 길이를 1 이상 20 이하가 아니면 생성할 수 없다.")
    void createLineWithNonValidColor(String color) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", color);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("노선 색의 길이는 1 이상 20 이하여야 합니다.")
        );
    }

    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "초록색");
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "빨간색");
        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("같은 이름 혹은 색깔을 가진 노선이 이미 있습니다.")
        );
    }

    @DisplayName("기존에 존재하는 노선 색깔로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "초록색");
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "3호선");
        params2.put("color", "초록색");
        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("같은 이름 혹은 색깔을 가진 노선이 이미 있습니다.")
        );
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "초록색");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "5호선");
        params2.put("color", "보라색");
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
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("단건의 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        long createdId = createResponse.body().jsonPath().getLong("id");
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().getLong("id")).isEqualTo(createdId),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("초록색")
        );
    }

    @DisplayName("존재하지 않는 노선을 조회한다.")
    @Test
    void getNonExistLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("존재하지 않는 노선입니다")
        );
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "1호선");
        updateParam.put("color", "파란색");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> findLineResponse = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then().log().all()
                .extract();
        LineResponse findLine = findLineResponse.body().jsonPath().getObject(".", LineResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(findLine.getName()).isEqualTo("1호선"),
                () -> assertThat(findLine.getColor()).isEqualTo("파란색")
        );
    }

    @ParameterizedTest(name = "{displayName} : {arguments}")
    @ValueSource(strings = {"", "12345678901"})
    @DisplayName("지하철 노선 이름의 길이를 1 이상 10 이하가 아니면 수정할 수 없다.")
    void updateLineNameFail(String name) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", name);
        updateParam.put("color", "파란색");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("노선 이름의 길이는 1 이상 10 이하여야 합니다.")
        );
    }

    @ParameterizedTest(name = "{displayName} : {arguments}")
    @ValueSource(strings = {"", "123456789012345678901"})
    @DisplayName("지하철 노선 색의 길이를 1 이상 20 이하가 아니면 수정할 수 없다.")
    void updateLineColorFail(String color) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "2호선");
        updateParam.put("color", color);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("노선 색의 길이는 1 이상 20 이하여야 합니다.")
        );
    }

    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "1호선");
        params1.put("color", "파란색");
        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "1호선");
        updateParam.put("color", "초록색");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("같은 이름 혹은 색깔을 가진 노선이 이미 있습니다.")
        );
    }

    @DisplayName("기존에 존재하는 노선 색깔로 지하철 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateColor() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "1호선");
        params1.put("color", "파란색");
        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "2호선");
        updateParam.put("color", "파란색");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("같은 이름 혹은 색깔을 가진 노선이 이미 있습니다.")
        );
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    void updateNonExistLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.body().jsonPath().getString("message"))
                        .isEqualTo("존재하지 않는 노선입니다")
        );
    }

    @DisplayName("노선을 제거한다")
    @Test
    void deleteById() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
