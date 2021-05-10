package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.station.controller.dto.StationRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("지하철 노선 관련 기능")
@Transactional
public class LineAcceptanceTest extends AcceptanceTest {


    private final LineRequest lineRequest1;
    private final LineRequest lineRequest2;
    private final StationRequest stationRequest;

    public LineAcceptanceTest() {
        this.lineRequest1 = new LineRequest("2호선", "bg-red-600", 0L, 0L, 0);
        this.lineRequest2 = new LineRequest("3호선", "bg-red-600", 0L, 0L, 0);
        this.stationRequest = new StationRequest("역삼역");
    }

    @DisplayName("지하철 노선 생성한다.")
    @Test
    void createLine() {
        // given

        //when
        ExtractableResponse<Response> response = createLine(lineRequest1);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("지하철 노선 목록을 보여준다.")
    void showLines() {
        // given
        ExtractableResponse<Response> createResponse1 = createLine(lineRequest1);
        ExtractableResponse<Response> createResponse2 = createStation(stationRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
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

    @DisplayName("지하철 노선 1개를 보여준다.")
    @Test
    void showLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine(lineRequest1);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {

        // given
        createLine(lineRequest1);
        ExtractableResponse<Response> createResponse = createLine(lineRequest2);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .body("size()", is(1));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine(lineRequest1);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createStation(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

}
