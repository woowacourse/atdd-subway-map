package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void beforeEach() {
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("정자역");
        StationRequest stationRequest4 = new StationRequest("선릉역");

        postStations(stationRequest1);
        postStations(stationRequest2);
        postStations(stationRequest3);
        postStations(stationRequest4);
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = postLines(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    void createLineWithDuplicateName() {
        // given
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        postLines(lineRequest1);

        // when
        LineRequest lineRequest2 = new LineRequest("신분당선", "bg-green-600", 3L, 4L, 10);
        ExtractableResponse<Response> response = postLines(lineRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("기존에 존재하는 노선의 색으로 노선을 생성한다.")
    void createLineWithDuplicateColor() {
        // given
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        postLines(lineRequest1);

        // when
        LineRequest lineRequest2 = new LineRequest("분당선", "bg-red-600", 3L, 4L, 10);
        ExtractableResponse<Response> response = postLines(lineRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선 목록을 조회한다.")
    void getLines() {
        /// given
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse1 = postLines(lineRequest1);

        LineRequest lineRequest2 = new LineRequest("2호선", "bg-green-600", 3L, 4L, 10);
        ExtractableResponse<Response> createResponse2 = postLines(lineRequest2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("노선을 하나 조회한다.")
    void getLine() {
        /// given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = postLines(lineRequest);
        long expectId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + expectId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void updateLine() {
        /// given
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = postLines(lineRequest1);
        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        LineRequest lineRequest2 = new LineRequest("다른분당선", "bg-blue-600");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 제거한다.")
    void deleteLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse = postLines(lineRequest);

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
}
