package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    LineDao lineDao;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");

        ExtractableResponse<Response> response = requestPost(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");

        requestPost(lineRequest);

        lineRequest = new LineRequest("2호선", "분홍색");

        ExtractableResponse<Response> response = requestPost(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선 색상으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");

        requestPost(lineRequest);

        lineRequest = new LineRequest("3호선", "초록색");

        ExtractableResponse<Response> response = requestPost(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 지하철 노선을 조회한다.")
    @Test
    void getLines() {
        LineRequest lineRequest1 = new LineRequest("1호선", "군청색");
        requestPost(lineRequest1);

        LineRequest lineRequest2 = new LineRequest("2호선", "초록색");
        requestPost(lineRequest2);

        ExtractableResponse<Response> response = requestGet();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList(".", LineResponse.class).size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 id로 조회한다.")
    @Test
    void getLine() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        ExtractableResponse<Response> createResponse = requestPost(lineRequest);

        Long id = getId(createResponse);
        ExtractableResponse<Response> response = requestGet(id);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long actual = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(actual).isEqualTo(id);
    }

    @DisplayName("특정 id를 가지는 노선을 수정한다.")
    @Test
    void updateLine() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        ExtractableResponse<Response> createResponse = requestPost(lineRequest);

        Long id = getId(createResponse);

        LineRequest lineRequest2 = new LineRequest("1호선", "군청색");
        RestAssured.given().log().all()
                .body(lineRequest2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all();

        ExtractableResponse<Response> response = requestGet(id);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        String actualName = response.jsonPath().getObject(".", LineResponse.class).getName();
        String actualColor = response.jsonPath().getObject(".", LineResponse.class).getColor();
        assertThat(actualName).isEqualTo("1호선");
        assertThat(actualColor).isEqualTo("군청색");
    }

    @DisplayName("특정 id의 노선을 삭제한다.")
    @Test
    void deleteLine() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        ExtractableResponse<Response> createResponse = requestPost(lineRequest);

        long id = getId(createResponse);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> readResponse = requestGet();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(readResponse.jsonPath().getList(".").size()).isEqualTo(0);
    }

    @DisplayName("지하철 노선 이름이나 색으로 null 또는 공백이 올 수 없다.")
    @ParameterizedTest
    @CsvSource(value = {",", "'',''", "' ',' '"})
    void notAllowNullOrBlankNameAndColor(String name, String color) {
        LineRequest lineRequest = new LineRequest(name, color);

        ExtractableResponse<Response> response = requestPost(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> requestPost(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGet() {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGet(Long id) {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private long getId(ExtractableResponse<Response> createResponse) {
        return Long.parseLong(createResponse.header("Location").split("/")[2]);
    }
}
