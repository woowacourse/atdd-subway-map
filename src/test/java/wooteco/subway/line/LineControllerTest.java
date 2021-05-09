package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.ControllerTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("노선 관련 기능")
class LineControllerTest extends ControllerTest {

    @DisplayName("노선 생성 - 성공")
    @Test
    void createLine() {
        노선_생성("신분당선", "bg-red-600").statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo("신분당선"))
                .body("color", equalTo("bg-red-600"));
    }

    private ValidatableResponse 노선_생성(String name, String color) {
        // given
        LineRequest lineRequest = new LineRequest(name, color);

        // when and then
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all();
    }

    @DisplayName("노선 생성 - 실패(이름 중복)")
    @Test
    void createLine_duplicatedName() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        노선_생성("신분당선", "bg-red-600");

        // when
        String uri = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .header("Location");
        // then
        assertThat(uri).isBlank();
    }

    @DisplayName("노선 생성 - 실패(필수 값 누락)")
    @Test
    void createLine_nullElement() {
        // given
        노선_생성("신분당선", "").statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("필수 요소가 누락 되었습니다."));
    }

    @DisplayName("노선 목록 조회 - 성공")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = 노선_생성("신분당선", "bg-red-600").extract();
        ExtractableResponse<Response> createResponse2 = 노선_생성("2호선", "bg-green-600").extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        // then
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
        ExtractableResponse<Response> createResponse = 노선_생성("신분당선", "bg-red-600").extract();

        // when and then
        RestAssured.given().log().all()
                .when()
                .get(createResponse.header("Location"))
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("id", notNullValue())
                .body("name", equalTo("신분당선"))
                .body("color", equalTo("bg-red-600"))
                .extract()
                .header("Location");
    }

    @DisplayName("노선 조회 - 실패(노선 정보 없음)")
    @Test
    void getStationById_notFound() {
        // when and then
        RestAssured.given().log().all()
                .when()
                .get("/lines/-1")
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(equalTo("노선을 찾을 수 없습니다."));
    }

    @DisplayName("노선 수정 - 성공")
    @Test
    void updateLine() {
        /// given
        String uri = 노선_생성("신분당선", "bg-red-600").extract().header("Location");
        LineRequest lineRequest = new LineRequest("구분당선", "bg-blue-600");

        // when
        RestAssured.given().log().all()
                .body(lineRequest)
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
        // given
        String uri = 노선_생성("신분당선", "bg-red-600").extract().header("Location");
        노선_생성("구분당선", "bg-blue-600");
        LineRequest lineRequest = new LineRequest("구분당선", "bg-blue-600");

        // when
        RestAssured.given().log().all()
                .body(lineRequest)
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
        String uri = 노선_생성("신분당선", "bg-red-600").extract().header("Location");

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
