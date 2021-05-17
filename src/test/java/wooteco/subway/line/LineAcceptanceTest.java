package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.exception.line.LineDuplicationException;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LineWithStationsResponse;
import wooteco.subway.station.dto.StationRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철노선 관련 기능")
@Sql("/truncate.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    private final LineRequest lineRequest =
        new LineRequest("2호선", "초록색", 1L, 2L, 10);

    @BeforeEach
    void setup() {
        stationPostRequest(new StationRequest("강남역"));
        stationPostRequest(new StationRequest("잠실역"));
        stationPostRequest(new StationRequest("강변역"));
        stationPostRequest(new StationRequest("구의역"));
    }


    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        ExtractableResponse<Response> response = linePostRequest(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성하면 예외를 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        linePostRequest(lineRequest);
        ExtractableResponse<Response> response = linePostRequest(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
            .isEqualTo(new LineDuplicationException().getMessage());
    }

    @DisplayName("기존에 존재하는 지하철노선 색깔로 지하철노선을 생성하면 예외를 발생한다.")
    @Test
    void createLineWithDuplicateColor() {
        linePostRequest(lineRequest);
        LineRequest afterRequest = new LineRequest("3호선", "초록색", 1L, 2L, 5);
        ExtractableResponse<Response> response = linePostRequest(afterRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
            .isEqualTo(new LineDuplicationException().getMessage());
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void findAllLines() {
        ExtractableResponse<Response> createResponse1 = linePostRequest(lineRequest);

        LineRequest lineRequest2 = new LineRequest("3호선", "주황색", 1L, 2L, 5);
        ExtractableResponse<Response> createResponse2 = linePostRequest(lineRequest2);

        ExtractableResponse<Response> response = lineGetRequest("/lines");

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsExactlyElementsOf(expectedLineIds);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void findLine() {
        createStation();
        ExtractableResponse<Response> createResponse = linePostRequest(lineRequest);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = lineGetRequest(uri);

        LineWithStationsResponse expectedResponse = createResponse.jsonPath().getObject(".", LineWithStationsResponse.class);
        LineWithStationsResponse resultResponse = response.jsonPath().getObject(".", LineWithStationsResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultResponse).usingRecursiveComparison()
            .ignoringFields("stations")
            .isEqualTo(expectedResponse);
    }

    private void createStation() {
        StationRequest stationRequest = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        RestAssured.given().log().all()
            .body(stationRequest2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
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

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 수정하면 예외를 발생한다.")
    @Test
    void updateLineWithDuplicatedName() {
        ExtractableResponse<Response> createResponse = linePostRequest(lineRequest);

        LineRequest lineRequest2 = new LineRequest("3호선", "주황색", 4L, 5L, 10);
        linePostRequest(lineRequest2);

        LineRequest updateLineRequest = new LineRequest("3호선", "초록색", 4L, 5L, 10);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
            .isEqualTo(new LineDuplicationException().getMessage());
    }

    @DisplayName("기존에 존재하는 지하철노선 색깔로 지하철노선을 수정하면 예외를 발생한다.")
    @Test
    void updateLineWithDuplicatedColor() {
        ExtractableResponse<Response> createResponse = linePostRequest(lineRequest);

        LineRequest lineRequest2 = new LineRequest("3호선", "주황색", 4L, 5L, 10);
        linePostRequest(lineRequest2);

        LineRequest updateLineRequest = new LineRequest("2호선", "주황색", 4L, 5L, 10);

        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString())
            .isEqualTo(new LineDuplicationException().getMessage());
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine() {
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

    private ExtractableResponse<Response> stationPostRequest(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> lineGetRequest(String url) {
        return RestAssured.given().log().all()
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }
}
