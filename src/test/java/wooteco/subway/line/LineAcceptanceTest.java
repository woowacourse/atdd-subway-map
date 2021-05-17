package wooteco.subway.line;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineRequest lineRequest1;
    private LineRequest lineRequest2;

    @BeforeEach
    void init() {
        lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        lineRequest2 = new LineRequest("2호선", "bg-green-600", 2L, 3L, 15);
    }

    private ExtractableResponse<Response> postLines(LineRequest params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = postLines(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    @DisplayName("지하철 노선 이름은 XX선으로 끝나지 않으면 예외처리가 된다.")
    @Test
    void invalidLineName() {
        // given
        LineRequest invalidRequest = new LineRequest("노선은선이라는단어로끝나야함", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = postLines(invalidRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("지하철 노선 이름이 잘못되었습니다.");
    }

    @DisplayName("중복된 지하철 노선을 생성할 수 없다.")
    @Test
    void cannotCreateDuplicatedLine() {
        // when
        ExtractableResponse<Response> response = postLines(lineRequest1);
        ExtractableResponse<Response> response2 = postLines(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.body().asString()).isEqualTo("중복된 노선입니다.");
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = postLines(lineRequest1);
        ExtractableResponse<Response> createResponse2 = postLines(lineRequest2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(line -> Long.parseLong(line.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = postLines(lineRequest1);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("상행/하행 종점역이 같은 경우 예외처리한다.")
    @Test
    void duplicateStationsOfLine() {
        // given
        LineRequest invalidRequest = new LineRequest("7호선", "bg-red-600", 1L, 1L, 10);

        // when
        ExtractableResponse<Response> response = postLines(invalidRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("중복된 지하철 역입니다.");
    }
}
