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
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dto.StationRequest;

@DisplayName("Line api")
@Sql("classpath:tableInit.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> postLineRequest(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> postStations(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void create() {
        postStations(new StationRequest("쌍문역"));
        postStations(new StationRequest("수유역"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);

        ExtractableResponse<Response> response = postLineRequest(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선 생성시 상행 종점과 하행 종점 거리가 0보다 크지 않으면, 노선을 생성할 수 없다.")
    @Test
    void createSectionDistanceException() {
        postStations(new StationRequest("쌍문역"));
        postStations(new StationRequest("수유역"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 0);

        ExtractableResponse<Response> response = postLineRequest(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 생성시 상행/하행 종점역이 등록되지 않는 역이라면, 노선을 생성할 수 없다.")
    @Test
    void createNotExistStationException() {
        postStations(new StationRequest("쌍문역"));
        postStations(new StationRequest("수유역"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 3L, 10);

        ExtractableResponse<Response> response = postLineRequest(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선 생성시 노선 이름이 중복이라면, 노선을 생성할 수 없다.")
    @Test
    void createLineWithDuplicateName() {
        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600");

        postLineRequest(lineRequest);
        ExtractableResponse<Response> response = postLineRequest(lineRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        postStations(new StationRequest("쌍문역"));
        postStations(new StationRequest("수유역"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse1 = postLineRequest(lineRequest);

        lineRequest = new LineRequest("3호선", "bg-orange-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse2 = postLineRequest(lineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    public void getLine() {
        /// given
        postStations(new StationRequest("쌍문역"));
        postStations(new StationRequest("수유역"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
        postLineRequest(lineRequest);

        lineRequest = new LineRequest("3호선", "bg-orange-600", 1L, 2L, 10);
        postLineRequest(lineRequest);

        // when
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

    @DisplayName("노선을 수정한다.")
    @Test
    public void putLine() {
        /// given
        postStations(new StationRequest("쌍문역"));
        postStations(new StationRequest("수유역"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);

        postLineRequest(lineRequest);
        lineRequest = new LineRequest("3호선", "bg-orange-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when()
            .put("/lines/1")
            .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    public void deleteLine() {
        // given
        postStations(new StationRequest("쌍문역"));
        postStations(new StationRequest("수유역"));

        LineRequest lineRequest = new LineRequest("2호선", "bg-green-600", 1L, 2L, 10);
        postLineRequest(lineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}