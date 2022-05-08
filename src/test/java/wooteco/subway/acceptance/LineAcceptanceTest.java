package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private final StationRequest stationRequest1 = new StationRequest("강남역");
    private final StationRequest stationRequest2 = new StationRequest("역삼역");
    private final StationRequest stationRequest3 = new StationRequest("분당역");
    private final LineRequest lineRequest1 =
            new LineRequest("신분당선", "bg-red-600", 1L, 2L, 20);
    private final LineRequest lineRequest2 =
            new LineRequest("분당선", "bg-green-600", 1L, 3L, 15);

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // when
        ExtractableResponse<Response> response = createLineResponse(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성할 때 예외를 발생시킨다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        createLineResponse(lineRequest1);

        // when
        ExtractableResponse<Response> response = createLineResponse(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        createStationResponse(stationRequest3);
        ExtractableResponse<Response> createResponse1 = createLineResponse(lineRequest1);
        ExtractableResponse<Response> createResponse2 = createLineResponse(lineRequest2);

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
        RestAssured.given().log().all()
                .when()
                .get("/lines/")
                .then().log().all()
                .body("name", contains(lineRequest1.getName(), lineRequest2.getName()))
                .body("color", contains(lineRequest1.getColor(), lineRequest2.getColor()));
    }

    @DisplayName("지하철 단일 노선을 조회한다.")
    @Test
    void getLineById() {
        /// given
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest1);
        int expectedLineId = Integer.parseInt(createResponse.header("Location").split("/")[2]);

        // when
        // then
        RestAssured.given().log().all()
                .when()
                .get("/lines/" + expectedLineId)
                .then().log().all()
                .body("id", equalTo(expectedLineId))
                .body("name", equalTo(lineRequest1.getName()))
                .body("color", equalTo(lineRequest1.getColor()));
    }

    @DisplayName("존재하지 않는 id로 지하철 단일 노선을 조회할 때 예외를 발생시킨다.")
    @Test
    void getLineByInvalidId() {
        /// given
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest1);

        // when
        int resultLineId = Integer.parseInt(createResponse.header("Location").split("/")[2]) + 1;
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest1);
        // when
        int resultLineId = Integer.parseInt(createResponse.header("Location").split("/")[2]);
        RestAssured.given().log().all()
                .body(lineRequest2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        RestAssured.given().log().all()
                .when()
                .get("/lines/" + resultLineId)
                .then().log().all()
                .statusCode(equalTo(HttpStatus.OK.value()))
                .body("name", equalTo("분당선"))
                .body("color", equalTo("bg-green-600"));
    }

    @DisplayName("존재하지 않는 지하철노선을 수정할 때 예외를 발생시킨다.")
    @Test
    void updateInvalidLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest1);

        // when
        int resultLineId = Integer.parseInt(createResponse.header("Location").split("/")[2]) + 1;
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest1);

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

    @DisplayName("존재하지 않는 지하철을 삭제하려할 때 예외를 발생시킨다.")
    @Test
    void deleteInvalidStation() {
        // given
        ExtractableResponse<Response> createResponse = createLineResponse(lineRequest1);

        // when
        String uri = createResponse.header("Location");
        RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> createLineResponse(LineRequest lineRequest) {
        createStationResponse(stationRequest1);
        createStationResponse(stationRequest2);

        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createStationResponse(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
