package wooteco.subway.acceptance;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Line;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Line line = new Line("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long resultLineId = Long.parseLong(response.header("Location").split("/")[2]);
        get("/lines/" + resultLineId).then()
                .assertThat()
                .body("id", equalTo(resultLineId.intValue()))
                .body("name", equalTo("신분당선"))
                .body("color", equalTo("bg-red-600"));
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Line line = new Line("신분당선", "bg-red-600");
        RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        Line line1 = new Line("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(line1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Line line2 = new Line("분당선", "bg-green-600");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(line2)
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
        Long resultLineId1 = Long.parseLong(createResponse1.header("Location").split("/")[2]);
        Long resultLineId2 = Long.parseLong(createResponse2.header("Location").split("/")[2]);
        get("/lines/" + resultLineId1).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(resultLineId1.intValue()))
                .body("name", equalTo("신분당선"))
                .body("color", equalTo("bg-red-600"));
        get("/lines/" + resultLineId2).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(resultLineId2.intValue()))
                .body("name", equalTo("분당선"))
                .body("color", equalTo("bg-green-600"));
    }

    @DisplayName("지하철 단일 노선을 조회한다.")
    @Test
    void getLineById() {
        /// given
        Line line1 = new Line("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(line1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        Long resultLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // then
        get("/lines/" + resultLineId).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(resultLineId.intValue()))
                .body("name", equalTo("신분당선"))
                .body("color", equalTo("bg-red-600"));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Line line1 = new Line("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(line1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        Line line2 = new Line("다른분당선", "bg-red-600");
        Long resultLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(line2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        get("/lines/" + resultLineId).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(resultLineId.intValue()))
                .body("name", equalTo("다른분당선"))
                .body("color", equalTo("bg-red-600"));
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Line line1 = new Line("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(line1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        String resultLineId = uri.split("/")[2];
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        get("/lines/" + resultLineId).then()
                .assertThat()
                .body("message", equalTo("해당하는 노선이 존재하지 않습니다."));
    }
}
