package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Sql("classpath:tableInit.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철 노선 한개가 저장된다.")
    void create() {
        ExtractableResponse<Response> response = createLineAPI("2호선", "bg-green-600");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    void createLineWithDuplicateName() {
        createLineAPI("2호선", "bg-green-600");

        ExtractableResponse<Response> response = createLineAPI("2호선", "bg-green-600");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("지하철역 목록을 조회한다.")
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineAPI("2호선", "bg-green-600");
        ExtractableResponse<Response> createResponse2 = createLineAPI("3호선", "bg-orange-600");

        // when
        ExtractableResponse<Response> response = getLineAllAPI();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = getExpectedLineIds(createResponse1, createResponse2);
        List<Long> resultLineIds = getResultLineIds(response);

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("id를 이용하여 지하철역을 조회한다.")
    public void getLine() {
        /// given
        createLineAPI("2호선", "bg-green-600");
        createLineAPI("3호선", "bg-orange-600");

        // when // then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .body("id", is(1))
            .body("name", is("2호선"))
            .body("color", is("bg-green-600"))
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("id를 기준으로 노선을 수정한다.")
    public void putLine() {
        /// given
        createLineAPI("2호선", "bg-green-600");
        LineRequest lineRequest = new LineRequest("3호선", "bg-orange-600");

        // when
        ExtractableResponse<Response> response = updateLineAPI(lineRequest);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("id를 이용해 노선을 삭제한다")
    public void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineAPI("2호선", "bg-green-600");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = deleteLineAPI(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLineAPI(String name, String color) {
        LineRequest lineRequest = new LineRequest(name, color);

        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> deleteLineAPI(String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    private List<Long> getResultLineIds(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
    }

    private List<Long> getExpectedLineIds(ExtractableResponse<Response> createResponse1,
        ExtractableResponse<Response> createResponse2) {
        return Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> getLineAllAPI() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }

    private ExtractableResponse<Response> updateLineAPI(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .put("/lines/1")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();
    }
}