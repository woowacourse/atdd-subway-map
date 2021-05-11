package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    private final String notExistItemMessage = "[ERROR] 해당 아이템이 존재하지 않습니다.";
    private final String noInputMessage = "[ERROR] 입력값이 존재하지 않습니다.";
    private final String duplicateMessage = "[ERROR] 중복된 이름입니다.";
    private final LineRequest line2Request = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
    private final LineRequest line3Request = new LineRequest("3호선", "bg-orange-600", 1L, 3L, 13);

    @Test
    @DisplayName("지하철 노선 한개가 저장된다.")
    void create() {
        ExtractableResponse<Response> response = createLineAPI(line2Request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    void createLineWithDuplicateName() {
        createLineAPI(line2Request);

        ExtractableResponse<Response> response = createLineAPI(line2Request);
        checkedThenException(response, duplicateMessage);
    }

    @Test
    @DisplayName("이름에 null을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithNameDataIsNull() {
        //given
        String name = null;
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @Test
    @DisplayName("이름에 공백을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithNameDataIsSpace() {
        //given
        String name = "  ";
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @Test
    @DisplayName("컬러에 null을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithColorDataIsNull() {
        //given
        String name = "2호선";
        String color = null;
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @Test
    @DisplayName("컬러에 공백을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithColorDataIsSpace() {
        //given
        String name = "2호선";
        String color = " ";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @Test
    @DisplayName("upStationId에 null을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithUpStationIdDataIsNull() {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = null;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @Test
    @DisplayName("upStationId에 0을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithUpStationIdDataIsZero() {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = 0L;
        Long downStationId = 2L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @Test
    @DisplayName("downStationId에 null을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithDownStationIdDataIsNull() {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = null;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @Test
    @DisplayName("downStationId에 0을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithDownStationIdDataIsZero() {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = 0L;
        int distance = 10;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("downStationId에 0을 입력하여 노선을 생성하면 에러가 출력된다.")
    void createLineWithDistanceDataIsLessThanZero(int value) {
        //given
        String name = "2호선";
        String color = "bg-green-60";
        Long upStationId = 1L;
        Long downStationId = 2L;
        int distance = value;

        //when
        ExtractableResponse<Response> response = createLineAPI(
            new LineRequest(name, color, upStationId, downStationId, distance));

        //then
        checkedThenException(response, noInputMessage);
    }

    @Test
    @DisplayName("지하철역 목록을 조회한다.")
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineAPI(line2Request);
        ExtractableResponse<Response> createResponse2 = createLineAPI(line3Request);

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
        createLineAPI(line2Request);

        // when
        ExtractableResponse<Response> response = getLineAPI();

        LineResponse lineResponse = response.body().as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse).usingRecursiveComparison().isEqualTo(new LineResponse(1L, "2호선", "bg-green-600", 1L, 2L, 10));
    }

    @Test
    @DisplayName("없는 id를 이용하여 지하철역을 조회하면 에러가 출력된다.")
    public void getLineWithNotExistItem() {
        /// given

        // when
        ExtractableResponse<Response> response = getLineAPI();

        // then
        checkedThenException(response, notExistItemMessage);
    }

    @Test
    @DisplayName("id를 기준으로 노선을 수정한다.")
    public void putLine() {
        /// given
        createLineAPI(line2Request);

        // when
        ExtractableResponse<Response> response = updateLineAPI(line3Request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("없는 노선을 수정하려 하면 에러가 발생한다.")
    public void putLineWithNotExistItem() {
        // given

        // when
        ExtractableResponse<Response> response = updateLineAPI(line3Request);

        //then
        checkedThenException(response, notExistItemMessage);
    }

    @Test
    @DisplayName("기존에 있는 이름으로 노선을 수정시 에러가 발생한다.")
    public void putLinWhitDuplicateName() {
        /// given
        createLineAPI(line2Request);
        createLineAPI(line3Request);

        // when
        ExtractableResponse<Response> response = updateLineAPI(line3Request);

        //then
        checkedThenException(response, duplicateMessage);
    }

    @Test
    @DisplayName("id를 이용해 노선을 삭제한다")
    public void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineAPI(line2Request);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = deleteLineAPI(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLineAPI(LineRequest lineRequest) {
        return RestAssured.given()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> deleteLineAPI(String uri) {
        return RestAssured.given()
            .when()
            .delete(uri)
            .then()
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
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> updateLineAPI(LineRequest lineRequest) {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .put("/lines/1")
            .then()
            .extract();
    }

    private ExtractableResponse<Response> getLineAPI() {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then()
            .extract();
    }

    private void checkedThenException(ExtractableResponse<Response> response, String message) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo(message);
    }
}