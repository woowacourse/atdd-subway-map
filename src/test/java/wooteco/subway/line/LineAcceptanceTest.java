package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

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
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철노선을 생성한다.")
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineRequest givenLineRequest = new LineRequest("1호선", "bg-blue-100");

        RestAssured.given().log().all()
            .body(givenLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        LineRequest lineRequest = new LineRequest("1호선", "bg-red-101");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철선 색으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        LineRequest givenLineRequest = new LineRequest("3호선", "bg-red-404");

        RestAssured.given().log().all()
            .body(givenLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        LineRequest lineRequest = new LineRequest("4호선", "bg-red-404");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선을 전체 조회한다.")
    @Test
    void checkAllLines() {
        //given
        LineRequest givenLineRequest = new LineRequest("호남선", "bg-purple-404");

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
            .body(givenLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        LineRequest lineRequest = new LineRequest("백호선", "bg-purple-505");

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(response1, response2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    public void specificLine() {
        //given
        final String color = "bg-purple-405";
        final String name = "부산선";
        LineRequest givenLineRequest = new LineRequest(name, color);
        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(givenLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + responseId)
            .then().log().all()
            .extract();

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        //then
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 노선을 조회한다.")
    @Test
    public void voidLine() {
        //given

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + 999)
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    public void updateLine() {
        //given
        LineRequest givenLineRequest = new LineRequest("구미선", "bg-white-400");
        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(givenLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        //when
        final String color = "bg-purple-406";
        final String name = "대구선";
        LineRequest lineRequest = new LineRequest(name, color);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + responseId)
            .then().log().all()
            .extract();

        ExtractableResponse<Response> checkLineResponse = RestAssured.given().log().all()
            .when()
            .get("/lines/" + responseId)
            .then().log().all()
            .extract();

        LineResponse lineResponse = checkLineResponse.jsonPath().getObject(".", LineResponse.class);
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    public void updateVoidLine() {
        //given

        //when
        final String color = "bg-purple-406";
        final String name = "대구선";
        LineRequest lineRequest = new LineRequest(name, color);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + 999)
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 다른 노선이 사용중인 색깔로 바꾼다")
    @Test
    public void updateLineToExistedLine() {
        //given
        LineRequest givenLineRequest1 = new LineRequest("구미선", "bg-white-400");

        ExtractableResponse<Response> requestResponse = RestAssured.given().log().all()
            .body(givenLineRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        LineRequest givenLineRequest2 = new LineRequest("황천선", "bg-white-401");

        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(givenLineRequest2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        //when
        final String color = "bg-white-400";
        final String name = "대구선";
        LineRequest lineRequest = new LineRequest(name, color);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + responseId)
            .then().log().all()
            .extract();

        ExtractableResponse<Response> checkLineResponse = RestAssured.given().log().all()
            .when()
            .get("/lines/" + responseId)
            .then().log().all()
            .extract();

        LineResponse lineResponse = checkLineResponse.jsonPath().getObject(".", LineResponse.class);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("특정 노선을 삭제한다.")
    @Test
    public void deleteSpecificLine() {
        //given
        final String color = "bg-purple-511";
        final String name = "울산선";
        LineRequest lineRequest = new LineRequest(name, color);

        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/" + responseId)
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 노선을 삭제한다.")
    @Test
    public void deleteVoidLine() {
        //given

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/" + 999)
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
