package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineSaveRequest;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

class LineAcceptanceTest extends AcceptanceTest {

    private static final String DUPLICATE_LINE_ERROR_MESSAGE = "노선이 이미 있습니다";

    /*
     * given
     * 상행 종점을 생성한다.
     * 하행 종점을 생성한다.
     *
     * when
     * 노선을 생성한다.
     *
     * then
     * 생성된 노선 정보를 응답한다.
     * */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationResponse station1 = createStation("station1");
        StationResponse station2 = createStation("station2");

        // when
        String lineName = "line1";
        String lineColor = "color1";
        ExtractableResponse<Response> response = createLineAndReturnResponse(lineName, lineColor, station1.getId(),
                station2.getId(), 10);

        // then
        List<StationResponse> stations = response.body().jsonPath().getList("stations", StationResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo(lineName),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo(lineColor),
                () -> assertThat(stations).usingRecursiveComparison().isEqualTo(List.of(station1, station2))
        );
    }


    /*
     * given
     * 하행 종점만 생성되어 있다.
     *
     * when
     * 노선을 생성한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("하행 종점역만 생성된 상태에서 노선을 생성한다.")
    @Test
    void createLineWithNonExistLine() {
        // given
        long idWillBeDeleted = createStation("station1").getId();
        RestAssured.when()
                .delete("/stations/" + idWillBeDeleted);

        long downStationId = createStation("station2").getId();

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse("line1", "color1", idWillBeDeleted,
                downStationId, 10);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains(NOT_FOUND_ERROR_MESSAGE)
        );
    }

    /*
     * given
     * 상행역과 하행역이 생성되어 있다.
     *
     * when
     * 역 사이 거리를 0으로 노선을 생성한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("역 사이 거리를 1 미만으로 노선을 생성한다.")
    @Test
    void createLineWithDistanceZero() {
        // given
        long upStationId = createStation("station1").getId();
        long downStationId = createStation("station2").getId();

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse("line1", "color1", upStationId,
                downStationId,
                0);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains("거리")
        );
    }

    /*
     * given
     * 노선이 생성되어 있다.
     *
     * when
     * 기존에 생성된 노선과 같은 이름으로 노선을 생성한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        String lineName = "line1";
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();
        createLineAndReturnResponse(lineName, "color1", id1, id2, 10);

        long upStationId = createStation("station3").getId();
        long downStationId = createStation("station4").getId();

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse(lineName, "color2", upStationId,
                downStationId, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains(DUPLICATE_LINE_ERROR_MESSAGE)
        );
    }

    /*
     * given
     * 노선이 생성되어 있다.
     *
     * when
     * 기존에 생성된 노선과 같은 색깔로 노선을 생성한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("기존에 존재하는 노선 색깔로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        String lineColor = "color1";
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();

        LineSaveRequest lineRequest1 = new LineSaveRequest("line1", lineColor, id1, id2, 10);
        RestAssured.given()
                .body(lineRequest1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse("line2", lineColor, id1, id2, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains(DUPLICATE_LINE_ERROR_MESSAGE)
        );
    }

    @DisplayName("노선 이름을 빈 값으로 생성한다")
    @Test
    void createLineWithNameBlank() {
        // given
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();
        String name = "";

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse(name, "color1", id1, id2, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains(BLANK_OR_NULL_ERROR_MESSAGE)
        );
    }

    @DisplayName("노선 이름을 null로 생성한다")
    @Test
    void createLineWithNameNull() {
        // given
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();
        String name = null;

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse(name, "color1", id1, id2, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains(BLANK_OR_NULL_ERROR_MESSAGE)
        );
    }

    @DisplayName("노선 색깔을 빈 값으로 생성한다")
    @Test
    void createLineWithColorBlank() {
        // given
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();
        String color = "";

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse("line1", color, id1, id2, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains(BLANK_OR_NULL_ERROR_MESSAGE)
        );
    }

    @DisplayName("노선 색깔을 null로 생성한다")
    @Test
    void createLineWithColorNull() {
        // given
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();
        String color = null;

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse("line1", color, id1, id2, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains(BLANK_OR_NULL_ERROR_MESSAGE)
        );
    }

    @DisplayName("같은 역으로 노선을 생성한다.")
    @Test
    void createLineWithSameStation() {
        // given
        long id1 = createStation("station1").getId();

        // when
        ExtractableResponse<Response> response = createLineAndReturnResponse("line1", "color1", id1, id1, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains("같은 역")
        );
    }

    /*
     * given
     * 지하철 노선이 생성되어 있다.
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
        StationResponse station1 = createStation("지하철역1");
        StationResponse station2 = createStation("지하철역2");
        StationResponse station3 = createStation("지하철역3");
        StationResponse station4 = createStation("지하철역4");

        LineResponse line1 = createLine("노선1", "색깔1", station1.getId(),
                station2.getId(), 10);
        LineResponse line2 = createLine("노선2", "색깔2", station3.getId(),
                station4.getId(), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        List<LineResponse> lines = response.jsonPath().getList(".", LineResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lines).usingRecursiveComparison().isEqualTo(List.of(line1, line2))
        );
    }

    /*
     * given
     * 지하철 노선이 생성되어 있다.
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
        StationResponse upStation = createStation("서울역");
        StationResponse downStation = createStation("시청");
        LineResponse line = createLine("1호선", "파란색", upStation.getId(), downStation.getId(), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + line.getId())
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.as(LineResponse.class)).usingRecursiveComparison().isEqualTo(line)
        );
    }

    /*
     * given
     * 지하철 노선이 생성되어 있다.
     * 해당 노선을 삭제한다.
     *
     * when
     * 삭제된 노선을 조회한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("존재하지 않는 노선을 조회한다.")
    @Test
    void getNonExistLine() {
        // given
        long id1 = createStation("지하철역1").getId();
        long id2 = createStation("지하철역2").getId();
        String uri = createLineAndReturnResponse("노선", "색깔", id1, id2, 10)
                .header("Location");

        RestAssured.when().delete(uri);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().jsonPath().getString("message")).contains(NOT_FOUND_ERROR_MESSAGE);
    }

    /*
     * given
     * 지하철 노선이 생성되어 있다.
     *
     * when
     * 해당 노선 정보를 수정한다.
     *
     * then
     * 성공적으로 응답한다.
     * 조회된 노선이 수정한 정보로 업데이트 되어있다.
     * */
    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        // given
        long id1 = createStation("지하철역1").getId();
        long id2 = createStation("지하철역2").getId();
        LineResponse originLine = createLine("노선", "색깔", id1, id2, 10);

        // when
        LineUpdateRequest updateRequest = new LineUpdateRequest("changedName", "changedColor");
        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + originLine.getId())
                .then().log().all()
                .extract();

        // then
        LineResponse line = RestAssured.given().log().all()
                .when()
                .get("/lines/" + originLine.getId())
                .then().log().all()
                .extract()
                .as(LineResponse.class);

        assertAll(
                () -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(line).usingRecursiveComparison().isEqualTo(
                        new LineResponse(originLine.getId(), "changedName", "changedColor", originLine.getStations()))
        );
    }

    /*
     * given
     * 지하철 두 개가 생성되어 있다.
     *
     * when
     * 노선 하나의 이름을 이미 존재하는 노선 이름으로 수정한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given
        long id1 = createStation("지하철역1").getId();
        long id2 = createStation("지하철역2").getId();
        long id3 = createStation("지하철역3").getId();
        long id4 = createStation("지하철역4").getId();

        createLineAndReturnResponse("노선1", "색깔1", id1, id2, 10);
        String createdLinePath = createLineAndReturnResponse("노선2", "색깔2", id3, id4, 10).header("Location");

        // when
        LineUpdateRequest updateRequest = new LineUpdateRequest("노선1", "색깔1");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(createdLinePath)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).contains(DUPLICATE_LINE_ERROR_MESSAGE);
    }

    /*
     * given
     * 노선 두 개가 생성되어 있다.
     *
     * when
     * 하나의 노선을 기존에 존재하는 노선의 색깔로 수정한다.
     *
     * then
     * 예외를 응답한다.
     * */
    @DisplayName("기존에 존재하는 노선 색깔로 지하철 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateColor() {
        // given
        long id1 = createStation("서울역").getId();
        long id2 = createStation("신설동").getId();
        long id3 = createStation("시청").getId();

        createLineAndReturnResponse("1호선", "파란색", id1, id2, 10);
        String createdLinePath = createLineAndReturnResponse("2호선", "초록색", id2, id3, 10).header("Location");

        // when
        LineUpdateRequest duplicateColorUpdate = new LineUpdateRequest("2호선", "파란색");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(duplicateColorUpdate)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(createdLinePath)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message")).contains(DUPLICATE_LINE_ERROR_MESSAGE);
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    void updateNonExistLine() {
        // given
        long id1 = createStation("지하철역1").getId();
        long id2 = createStation("지하철역2").getId();
        String uri = createLineAndReturnResponse("노선", "색깔", id1, id2, 10)
                .header("Location");

        RestAssured.when().delete(uri);
        LineUpdateRequest updateRequest = new LineUpdateRequest("2호선", "파란색");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.body().jsonPath().getString("message")).contains(NOT_FOUND_ERROR_MESSAGE);
    }

    /*
     * given
     * 노선이 등록되어 있다.
     *
     * when
     * 노선을 제거한다.
     *
     * then
     * 성공을 응답한다.
     * 제거된 노선으로 조회하면 예외를 응답한다.
     * */
    @DisplayName("노선을 제거한다")
    @Test
    void deleteById() {
        // given
        long id1 = createStation("서울역").getId();
        long id2 = createStation("신설동").getId();
        String createdLinePath = createLineAndReturnResponse("1호선", "파란색", id1, id2, 10).header("Location");

        // when
        ExtractableResponse<Response> deleteResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(createdLinePath)
                .then().log().all()
                .extract();

        // then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(createdLinePath)
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value()),
                () -> assertThat(response.body().jsonPath().getString("message")).contains(NOT_FOUND_ERROR_MESSAGE)
        );
    }

    /*
     * given
     * station1, station3를 상행종점, 하행종점으로 하는 line1이 생성되어 있다.
     *
     * when
     * station2를 상행, station3을 하행으로 하는 거리 7짜리 구간을 추가한다.
     *
     * then
     * 성공 응답을 반환한다.
     * 구간 추가정보가 잘 적용됨.
     * */
    @DisplayName("구간을 추가한다")
    @Test
    void addSection() {
        // given
        StationResponse station1 = createStation("station1");
        StationResponse station3 = createStation("station3");
        LineResponse createdLine = createLine("line", "color", station1.getId(), station3.getId(), 10);

        StationResponse station2 = createStation("station2");
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 7);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().log().all()
                .post("/lines/" + createdLine.getId() + "/sections")
                .then().log().all()
                .extract();

        // then
        LineResponse line = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + createdLine.getId())
                .then()
                .extract()
                .as(LineResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(line).usingRecursiveComparison().isEqualTo(
                        new LineResponse(createdLine.getId(), createdLine.getName(), createdLine.getColor(),
                                List.of(station1, station2, station3)))
        );
    }

    /*
     * given
     * station1, station3를 상행종점, 하행종점으로 하는 line1이 생성되어 있다.
     * station2를 상행, station3을 하행으로 하는 거리 7짜리 구간을 추가한다.
     *
     * when
     * station2를 구간에서 제거한다.
     *
     * then
     * 성공 응답을 반환한다.
     * 구간 추가정보가 잘 적용됨.
     * */
    @DisplayName("구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        StationResponse station1 = createStation("station1");
        StationResponse station3 = createStation("station3");
        LineResponse createdLine = createLine("line", "color", station1.getId(), station3.getId(), 10);

        StationResponse station2 = createStation("station2");
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 7);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().log().all()
                .post("/lines/" + createdLine.getId() + "/sections")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().log().all().queryParam("stationId", station2.getId())
                .delete("/lines/" + createdLine.getId() + "/sections")
                .then().log().all()
                .extract();

        // then
        LineResponse line = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + createdLine.getId())
                .then()
                .extract()
                .as(LineResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(line).usingRecursiveComparison().isEqualTo(
                        new LineResponse(createdLine.getId(), createdLine.getName(), createdLine.getColor(),
                                List.of(station1, station3)))
        );
    }
}
