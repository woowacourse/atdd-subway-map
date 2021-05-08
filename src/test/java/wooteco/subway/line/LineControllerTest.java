package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.ControllerTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("노선 관련 기능")
class LineControllerTest extends ControllerTest {

    @DisplayName("노선 생성 - 성공")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // then
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/" + lineResponse.getId());
        assertThat(lineResponse).usingRecursiveComparison()
                .isEqualTo(new LineResponse(lineResponse.getId(), "신분당선", "bg-red-600"));
    }

    @DisplayName("노선 생성 - 실패(이름 중복)")
    @Test
    void createLine_duplicatedName() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.header("Location")).isBlank();
    }

    @DisplayName("노선 목록 조회 - 성공")
    @Test
    void getLines() {
        /// given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        final ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        LineRequest lineRequest2 = new LineRequest("2호선", "bg-green-600");

        final ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(lineRequest2)
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
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse).usingRecursiveComparison()
                .isEqualTo(new LineResponse(lineResponse.getId(), "신분당선", "bg-red-600"));
    }

    @DisplayName("노선 조회 - 실패(노선 정보 없음)")
    @Test
    void getStationById_notFound() {
        /// given
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/-1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 수정 - 성공")
    @Test
    void updateLine() {
        /// given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        final String uri = RestAssured.given()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .extract().header("Location");

        LineRequest lineRequest2 = new LineRequest("구분당선", "bg-blue-600");

        // when
        RestAssured.given().log().all()
            .body(lineRequest2)
            .contentType(ContentType.JSON)
            .when()
            .put(uri)
            .then().log().all()
            .statusCode(HttpStatus.OK.value());

        // then
        RestAssured.given().log().all()
            .when()
            .get(uri)

            .then().log().all()
            .body("name", equalTo("구분당선"))
            .body("color", equalTo("bg-blue-600"));
    }

    @DisplayName("노선 수정 - 실패(변경하려는 노선 이름 중복)")
    @Test
    void updateLine_duplicatedName() {
        /// given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        final String uri = RestAssured.given()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .extract().header("Location");

        LineRequest lineRequest2 = new LineRequest("구분당선", "bg-red-600");

        RestAssured.given()
            .body(lineRequest2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines");

        LineRequest lineRequest3 = new LineRequest("구분당선", "bg-blue-600");

        // when
        RestAssured
            .given().log().all()
            .body(lineRequest3)
            .contentType(ContentType.JSON)

            .when()
            .put(uri)

            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(equalTo("이미 등록되어 있는 노선 이름입니다."));
    }

    @DisplayName("노선 수정 - 실패(존재 하지 않는 노선 수정)")
    @Test
    void updateLine_notFound() {
        /// given
        LineRequest lineRequest = new LineRequest("구분당선", "bg-blue-600");

        // when
        RestAssured
            .given().log().all()
            .body(lineRequest)
            .contentType(ContentType.JSON)

            .when()
            .put("/lines/-1")

            .then().log().all()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 삭제 - 성공")
    @Test
    void removeLine() {
        /// given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");

        final String uri = RestAssured.given()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .extract().header("Location");

        // when
        RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());

        // then
        RestAssured.given().log().all()
            .when()
            .get(uri)

            .then().log().all()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
