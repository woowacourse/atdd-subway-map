package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.common.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.execute("truncate table LINE");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> response = createLineInsertResponse("초록색", "2호선");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("유효성 검사에 걸리는 이름과 색상의 지하철 노선을 생선한다.")
    @Test
    void createInValidLine() {
        ExtractableResponse<Response> response = createLineInsertResponse(" ", " ");
        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getDetail()).contains("Validation failed for argument [0] in public org.springframework.http.ResponseEntity<wooteco.subway.line.LineResponse> wooteco.subway.line.LineController.createLine(wooteco.subway.line.LineRequest) with 2 errors:");
        assertThat(errorResponse.getMessage()).isEqualTo("VALIDATION_FAILED");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given & when
        createLineInsertResponse("초록색", "2호선");
        ExtractableResponse<Response> response = createLineInsertResponse("초록색", "2호선");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("존재하는 노선 이름입니다.");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineInsertResponse("초록색", "2호선");
        ExtractableResponse<Response> createResponse2 = createLineInsertResponse("파란색", "1호선");

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                .when()
                .get("/lines")
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location")
                        .split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 1개를 조회한다.")
    @Test
    void getLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse("초록색", "2호선");
        String uri = extract.header("Location");

        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .extract();

        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("초록색");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선 1개를 조회한다.")
    @Test
    void findNotExistingLineByName() {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then()
                .extract();

        assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("노선을 찾을 수 없습니다.");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse("초록색", "2호선");
        String uri = extract.header("Location");
        LineRequest lineRequest = new LineRequest("9호선", "남색", 0L, 0L, 0);
        ExtractableResponse<Response> response = RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("유효하지 않은 값으로 지하철 노선을 수정한다.")
    @Test
    public void modifyWithInValidLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse("초록색", "2호선");
        String uri = extract.header("Location");

        LineRequest lineRequest = new LineRequest(" ", " ", 0L, 0L, 0);
        ExtractableResponse<Response> response = RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then()
                .extract();

        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getDetail()).contains("Validation failed for argument [1] in public org.springframework.http.ResponseEntity<java.lang.Void> wooteco.subway.line.LineController.modifyLine(java.lang.Long,wooteco.subway.line.LineRequest) with 2 errors:");
        assertThat(errorResponse.getMessage()).isEqualTo("VALIDATION_FAILED");
    }

    @DisplayName("지하철 노선을 삭제한다")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse("2호선", "초록색");

        String uri = extract.header("Location");
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLineInsertResponse(String color, String name) {
        LineRequest lineRequest = new LineRequest(name, color, null, null, 0);

        return RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
    }
}