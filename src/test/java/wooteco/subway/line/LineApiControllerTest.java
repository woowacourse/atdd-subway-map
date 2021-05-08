package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.web.LineRequest;
import wooteco.subway.line.web.LineResponse;

@DisplayName("노선 관련 기능")
class LineApiControllerTest extends AcceptanceTest {

    @DisplayName("노선 생성 - 성공")
    @Test
    void createLine() {
        // given
        final String lineName = "신분당선";
        final String lineColor = "bg-red-600";
        final LineRequest lineRequest = LineRequest.create(lineName, lineColor);

        // when
        final ExtractableResponse<Response> result = 노선_생성(lineRequest);

        //then
        final LineResponse lineResponse = result.body().as(LineResponse.class);

        assertThat(result.header("Location")).isNotEmpty();
        assertThat(lineResponse.getName()).isEqualTo(lineName);
        assertThat(lineResponse.getColor()).isEqualTo(lineColor);
    }

    @DisplayName("노선 생성 - 실패(이름 중복)")
    @Test
    void createLine_duplicatedName() {
        // given
        final String lineName = "신분당선";
        final String color = "bg-red-600";
        노선_생성(LineRequest.create(lineName, color));

        // when
        final ExtractableResponse<Response> result =
            노선_생성(LineRequest.create(lineName, color));

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록 조회 - 성공")
    @Test
    void getLines() {
        /// given
        final LineRequest lineRequest1 = LineRequest.create("신분당선", "bg-red-600");
        final LineRequest lineRequest2 = LineRequest.create("2호선", "bg-green-600");
        노선_생성(lineRequest1);
        노선_생성(lineRequest2);

        // when
        final ExtractableResponse<Response> result = 노선_조회();

        // then
        final List<LineResponse> response = Arrays.asList(result.body().as(LineResponse[].class));

        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response).hasSize(2);
        assertThat(response).extracting(LineResponse::getName).containsExactlyInAnyOrder("신분당선","2호선");
        assertThat(response).extracting(LineResponse::getColor).containsExactlyInAnyOrder("bg-red-600","bg-green-600");
    }

    @DisplayName("한 노선 조회 - 성공")
    @Test
    void getLineById() {
        // given
        final LineRequest lineRequest = LineRequest.create("신분당선", "bg-red-600");
        final ExtractableResponse<Response> createResponse =
            노선_생성(lineRequest);


        // when
        int lineId = createResponse.body().path("id");
        ExtractableResponse<Response> response = 노선_조회((long) lineId);

        // then
        final LineResponse lineResponse = response.body().as(LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-red-600");
    }



    @DisplayName("노선 조회 - 실패(노선 정보 없음)")
    @Test
    void getStationById_notFound() {
        /// given
        ExtractableResponse<Response> response = 노선_조회(Long.MAX_VALUE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선 수정 - 성공")
    @Test
    void updateLine() {
        /// given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        final String uri = RestAssured.given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .extract().header("Location");

        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");

        // when
        RestAssured.given().log().all()
            .body(params)
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
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        final String uri = RestAssured.given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .extract().header("Location");

        params.put("color", "bg-red-600");
        params.put("name", "구분당선");
        RestAssured.given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines");

        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");

        // when
        RestAssured
            .given().log().all()
            .body(params)
            .contentType(ContentType.JSON)

            .when()
            .put(uri)

            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(equalTo("이미 등록되어 있는 노선 정보입니다."));
    }

    @DisplayName("노선 수정 - 실패(존재 하지 않는 노선 수정)")
    @Test
    void updateLine_notFound() {
        /// given
        Map<String, String> params = new HashMap<>();

        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");

        // when
        RestAssured
            .given().log().all()
            .body(params)
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
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        final String uri = RestAssured.given()
            .body(params)
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


    ExtractableResponse<Response> 노선_생성(LineRequest lineRequest) {
        return RestAssured
            .given()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    ExtractableResponse<Response> 노선_조회() {
        return RestAssured
            .given()
            .when()
            .get("/lines")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> 노선_조회(Long lineId) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/"+lineId)
            .then().log().all()
            .extract();
    }
}