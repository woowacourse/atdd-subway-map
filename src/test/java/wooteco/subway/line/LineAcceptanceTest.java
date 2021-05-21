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
import wooteco.subway.station.StationRequest;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Sql("/truncate.sql")
public class LineAcceptanceTest extends AcceptanceTest {
    private LineRequest 경의중앙선 =
            new LineRequest("경의중앙선", "하늘색", 1L, 2L, 10);
    private LineRequest 신분당선 =
            new LineRequest("신분당선", "빨간색", 4L, 5L, 10);

    private ExtractableResponse<Response> lineResponse1;
    private ExtractableResponse<Response> lineResponse2;

    @BeforeEach
    void initialize() {
        lineResponse1 = lineResponse(경의중앙선);
        lineResponse2 = lineResponse(신분당선);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        assertThat(lineResponse1.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse1.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        ExtractableResponse<Response> response = lineResponse(경의중앙선);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선 색깔로 노선을 생성하면 예외가 발생한다.")
    @Test
    void createLineWithDuplicateColor() {
        LineRequest 신분당선_불가능 =
                new LineRequest("신분당선", "하늘색", 4L, 5L, 10);
        ExtractableResponse<Response> response = lineResponse(신분당선_불가능);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void findAllLines() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> lineIds = Stream.of(lineResponse1, lineResponse2)
                .map(res -> Long.parseLong(res.header("Location").split("/")[2]))
                .collect(toList());
        List<Long> resultIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(toList());

        assertThat(resultIds).containsAll(lineIds);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void findLine() {
        createStation();

        String uri = lineResponse1.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse line = lineResponse1.jsonPath().getObject(".", LineResponse.class);
        LineResponse result = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("stations")
                .isEqualTo(line);
    }

    private void createStation() {
        StationRequest 탄현역 = new StationRequest("탄현역");
        StationRequest 일산역 = new StationRequest("일산역");

        stationResponse(탄현역);
        stationResponse(일산역);
    }

    private void stationResponse(StationRequest stationRequest) {
        RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        LineRequest 분당선 =
                new LineRequest("분당선", "노란색", 6L, 8L, 12);

        String uri = lineResponse1.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(분당선)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 수정하면 예외가 발생한다.")
    @Test
    void updateLineWithDuplicateName() {
        LineRequest 경의중앙선_불가능 =
                new LineRequest("신분당선", "하늘색", 4L, 5L, 10);

        String uri = lineResponse1.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(경의중앙선_불가능)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선 색깔로 노선을 수정하면 예외가 발생한다.")
    @Test
    void updateLineWithDuplicateColor() {
        LineRequest 경의중앙선_불가능 =
                new LineRequest("경의중앙선", "빨간색", 4L, 5L, 10);

        String uri = lineResponse1.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(경의중앙선_불가능)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        String uri = lineResponse1.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> lineResponse(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
