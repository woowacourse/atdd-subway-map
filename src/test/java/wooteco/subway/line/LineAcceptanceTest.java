package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.exception.line.LineDuplicationException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
@Sql("/truncate.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 5);
        ExtractableResponse<Response> response = linePostRequest(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 5);
        linePostRequest(lineRequest);

        ExtractableResponse<Response> response = linePostRequest(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
            .isEqualTo(new LineDuplicationException().getMessage());
    }

    @DisplayName("기존에 존재하는 지하철노선 색깔로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        LineRequest beforeRequest = new LineRequest("2호선", "초록색", 1L, 2L, 5);
        linePostRequest(beforeRequest);
        LineRequest afterRequest = new LineRequest("3호선", "초록색", 1L, 2L, 5);
        ExtractableResponse<Response> response = linePostRequest(afterRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
            .isEqualTo(new LineDuplicationException().getMessage());
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void findAllLines() {
        LineRequest lineRequest1 = new LineRequest("2호선", "초록색", 1L, 2L, 5);
        ExtractableResponse<Response> createResponse1 = linePostRequest(lineRequest1);

        LineRequest lineRequest2 = new LineRequest("3호선", "주황색", 1L, 2L, 5);
        ExtractableResponse<Response> createResponse2 = linePostRequest(lineRequest2);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsExactlyElementsOf(expectedLineIds);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findLine() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 5);
        ExtractableResponse<Response> createResponse = linePostRequest(lineRequest);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse expectedResponse = createResponse.body().as(LineResponse.class);
        LineResponse resultResponse = response.jsonPath().getList(".", LineResponse.class).get(0);

        assertThat(resultResponse.getId()).isEqualTo(expectedResponse.getId());
        assertThat(resultResponse.getName()).isEqualTo(expectedResponse.getName());
        assertThat(resultResponse.getColor()).isEqualTo(expectedResponse.getColor());
    }

    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 5);
        ExtractableResponse<Response> createResponse = linePostRequest(lineRequest);

        LineRequest lineUpdateRequest = new LineRequest("3호선", "주황색", 1L, 2L, 5);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine() {
        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 5);
        ExtractableResponse<Response> createResponse = linePostRequest(lineRequest);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> linePostRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }
}
