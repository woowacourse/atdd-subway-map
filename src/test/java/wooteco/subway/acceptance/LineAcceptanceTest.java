package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.ui.dto.ExceptionResponse;
import wooteco.subway.ui.dto.LineRequest;
import wooteco.subway.ui.dto.LineResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final LineRequest BOONDANGLINE_REQUEST = new LineRequest("신분당선", "bg-red-600");
    private static final LineRequest SECONDLINE_REQUEST = new LineRequest("2호선", "bg-green-600");

    @DisplayName("노선 생성 요청 시, 응답코드는 201 CREATED 이고 응답헤더에는 Location 이 있어야 한다")
    @Test
    void createLine() {
        // given
        final String newLineRequestJson = toJson(BOONDANGLINE_REQUEST);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newLineRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선 생성 시도 시 Bad request가 응답된다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        final String newLineRequestJson = toJson(BOONDANGLINE_REQUEST);

        RestAssured.given().log().all()
                .body(newLineRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newLineRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        final String newBoonDangLineRequestJson = toJson(BOONDANGLINE_REQUEST);

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(newBoonDangLineRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        final String newSecondLineRequestJson = toJson(SECONDLINE_REQUEST);

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(newSecondLineRequestJson)
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

    @DisplayName("노선을 제거 시 응답코드는 NO_CONTENT 이다. 존재하지 않는 노선 조회 시, 응답코드는 BAD_REQUEST 이다")
    @Test
    void deleteLine() {
        // given
        final String newLineRequestJson = toJson(BOONDANGLINE_REQUEST);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(newLineRequestJson)
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

        ExtractableResponse<Response> findResponseAfterDeletion = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then().log().all()
                .extract();
        final ExceptionResponse exceptionResponseForNotExists = findResponseAfterDeletion.jsonPath()
                .getObject(".", ExceptionResponse.class);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(findResponseAfterDeletion.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(exceptionResponseForNotExists.getMessage()).contains("요청한 노선이 존재하지 않습니다")
        );
    }

    @DisplayName("ID로 특정 노선의 정보를 조회할 수 있으며, 응답코드는 OK이다")
    @Test
    void getLine() {
        /// given
        final String newLineRequestJson = toJson(BOONDANGLINE_REQUEST);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(newLineRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        final LineResponse expected = createResponse.jsonPath().getObject(".", LineResponse.class);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(createResponse.header("Location"))
                .then().log().all()
                .extract();
        final LineResponse actual = createResponse.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }

    @DisplayName("ID로 특정 노선의 정보를 갱신할 수 있으며, 정상 갱신 시 응답코드는 OK이다")
    @Test
    void updateLine() {
        // given
        final String newLineRequestJson = toJson(BOONDANGLINE_REQUEST);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(newLineRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        final LineResponse expected = createResponse.jsonPath().getObject(".", LineResponse.class);

        // when
        String uri = createResponse.header("Location");
        final String updateRequestJson = toJson(SECONDLINE_REQUEST);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateRequestJson)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> updatedResponse = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
        final LineResponse actual = updatedResponse.jsonPath().getObject(".", LineResponse.class);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                () -> assertThat(actual.getName()).isEqualTo(SECONDLINE_REQUEST.getName()),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

}
