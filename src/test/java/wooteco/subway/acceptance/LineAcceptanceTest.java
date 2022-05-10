package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineRequestV2;
import wooteco.subway.dto.LineResponseV2;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    /*
     * given
     * 상행 종점을 등록한다.
     * 하행 종점을 등록한다.
     *
     * when
     * 노선을 등록한다.
     *
     * then
     * 등록된 노선 정보를 응답한다.
     * */
    @DisplayName("지하철 노선을 등록한다.")
    @Test
    void registerLine() {
        // given
        long upStationId = registerStationAndReturnId("서울역");
        long downStationId = registerStationAndReturnId("시청");

        // when
        ExtractableResponse<Response> response = registerLineAndReturnResponse(
                "1호선", "파란색", upStationId, downStationId, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("1호선"),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("파란색")
        );

        List<Long> expectedStationIds = List.of(upStationId, downStationId);
        List<Long> stationIds = response.body().jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsAll(expectedStationIds);
    }

    /*
     * given
     * 하행 종점만 등록되어 있다.
     *
     * when
     * 노선을 등록한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("하행 종점역만 등록된 상태에서 노선을 등록한다.")
    @Test
    void registerLineWithNonExistLine() {
        // given
        long idWillBeDeleted = registerStationAndReturnId("서울역");
        RestAssured.when()
                .delete("/stations/" + idWillBeDeleted);

        long downStationId = registerStationAndReturnId("시청");

        // when
        ExtractableResponse<Response> response = registerLineAndReturnResponse("1호선", "파란색", idWillBeDeleted,
                downStationId, 10);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
        );
    }

    /*
     * given
     * 상행역과 하행역이 등록되어 있다.
     *
     * when
     * 역 사이 거리를 0으로 노선을 등록한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("역 사이 거리를 1 미만으로 노선을 등록한다.")
    @Test
    void registerLineWithDistanceZero() {
        // given
        long upStationId = registerStationAndReturnId("서울역");
        long downStationId = registerStationAndReturnId("시청");

        // when
        ExtractableResponse<Response> response = registerLineAndReturnResponse("1호선", "파란색", upStationId, downStationId,
                0);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
        );
    }

    /*
     * given
     * 노선이 등록되어 있다.
     *
     * when
     * 기존에 등록된 노선과 같은 이름으로 노선을 등록한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 등록한다.")
    @Test
    void registerLineWithDuplicateName() {
        // given
        String lineName = "1호선";
        long id1 = registerStationAndReturnId("서울역");
        long id2 = registerStationAndReturnId("시청");
        registerLineAndReturnResponse(lineName, "파란색", id1, id2, 10);

        long upStationId = registerStationAndReturnId("사당");
        long downStationId = registerStationAndReturnId("성수");

        // when
        ExtractableResponse<Response> response = registerLineAndReturnResponse(lineName, "초록색", upStationId,
                downStationId, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
        );
    }

    /*
     * given
     * 노선이 등록되어 있다.
     *
     * when
     * 기존에 등록된 노선과 같은 색깔로 노선을 등록한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("기존에 존재하는 노선 색깔로 지하철 노선을 등록한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        String lineColor = "파란색";
        long id1 = registerStationAndReturnId("서울역");
        long id2 = registerStationAndReturnId("시청");

        LineRequestV2 lineRequest1 = new LineRequestV2("1호선", lineColor, id1, id2, 10);
        RestAssured.given().log().all()
                .body(lineRequest1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        long upStationId = registerStationAndReturnId("사당");
        long downStationId = registerStationAndReturnId("성수");

        // when
        ExtractableResponse<Response> response = registerLineAndReturnResponse("2호선", lineColor, upStationId,
                downStationId, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
        );
    }

    /*
     * given
     * 지하철 노선이 등록되어 있다.
     *
     * when
     * 지하철 노선 목록을 조회한다.
     *
     * then
     * 지하철 노선 목록을 응답한다.
     * */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        long id1 = registerStationAndReturnId("지하철역1");
        long id2 = registerStationAndReturnId("지하철역2");
        long id3 = registerStationAndReturnId("지하철역3");
        long id4 = registerStationAndReturnId("지하철역4");

        ExtractableResponse<Response> createResponse1 = registerLineAndReturnResponse("노선1", "색깔1", id1, id2, 10);
        ExtractableResponse<Response> createResponse2 = registerLineAndReturnResponse("노선2", "색깔2", id3, id4, 10);

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
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponseV2.class).stream()
                .map(LineResponseV2::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);

        List<LineResponseV2> responseEntities = response.jsonPath().getList(".", LineResponseV2.class);

        ArrayList<String> stationNames = new ArrayList<>();
        for (LineResponseV2 responseEntity : responseEntities) {
            stationNames.addAll(
                    responseEntity.getStations().stream().map(StationResponse::getName).collect(Collectors.toList()));
        }
        assertThat(stationNames).containsExactly("지하철역1", "지하철역2", "지하철역3", "지하철역4");

    }

    /*
     * given
     * 지하철 노선이 등록되어 있다.
     *
     * when
     * 지하철 노선을 조회한다.
     *
     * then
     * 지하철 노선 단건을 응답한다.
     * */
    @DisplayName("단건의 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        long upStationId = registerStationAndReturnId("서울역");
        long downStationId = registerStationAndReturnId("시청");
        ExtractableResponse<Response> createResponse = registerLineAndReturnResponse(
                "1호선", "파란색", upStationId, downStationId, 10);
        long createdLineId = createResponse.body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + createdLineId)
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("1호선"),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo("파란색")
        );

        List<Long> expectedStationIds = List.of(upStationId, downStationId);
        List<Long> stationIds = response.body().jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsAll(expectedStationIds);
    }

    @DisplayName("존재하지 않는 노선을 조회한다.")
    @Test
    void getNonExistLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().jsonPath().getString("message")).isNotBlank();
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        LineRequest updateRequest = new LineRequest("1호선", "파란색");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given
        LineRequest previouslySaved1 = new LineRequest("1호선", "파란색");
        RestAssured.given().log().all()
                .body(previouslySaved1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        LineRequest previouslySaved2 = new LineRequest("2호선", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(previouslySaved2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        LineRequest duplicateNameUpdate = new LineRequest("1호선", "초록색");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(duplicateNameUpdate)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 색깔로 지하철 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateColor() {
        // given
        LineRequest previouslySaved1 = new LineRequest("1호선", "파란색");
        RestAssured.given().log().all()
                .body(previouslySaved1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        LineRequest previouslySaved2 = new LineRequest("2호선", "초록색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(previouslySaved2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        LineRequest duplicateColorUpdate = new LineRequest("2호선", "파란색");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(duplicateColorUpdate)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).isNotBlank();
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    void updateNonExistLine() {
        // given
        LineRequest updateRequest = new LineRequest("2호선", "파란색");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().jsonPath().getString("message")).isNotBlank();
    }

    @DisplayName("노선을 제거한다")
    @Test
    void deleteById() {
        // given
        LineRequest deleteRequest = new LineRequest("2호선", "파란색");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(deleteRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
