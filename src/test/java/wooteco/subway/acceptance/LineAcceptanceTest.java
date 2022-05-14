package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineUpdateRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

class LineAcceptanceTest extends AcceptanceTest {

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
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();

        // when
        String lineName = "line1";
        String lineColor = "color1";
        ExtractableResponse<Response> response = createLineAndReturnResponse(lineName, lineColor, id1, id2, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo(lineName),
                () -> assertThat(response.body().jsonPath().getString("color")).isEqualTo(lineColor)
        );

        List<Long> expectedStationIds = List.of(id1, id2);
        List<Long> stationIds = response.body().jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(stationIds).containsAll(expectedStationIds);
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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

        LineRequest lineRequest1 = new LineRequest("line1", lineColor, id1, id2, 10);
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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
                () -> assertThat(response.body().jsonPath().getString("message")).isNotBlank()
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
        long id1 = createStation("지하철역1").getId();
        long id2 = createStation("지하철역2").getId();
        long id3 = createStation("지하철역3").getId();
        long id4 = createStation("지하철역4").getId();

        ExtractableResponse<Response> createResponse1 = createLineAndReturnResponse("노선1", "색깔1", id1, id2, 10);
        ExtractableResponse<Response> createResponse2 = createLineAndReturnResponse("노선2", "색깔2", id3, id4, 10);

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

        List<LineResponse> responseEntities = response.jsonPath().getList(".", LineResponse.class);

        ArrayList<String> stationNames = new ArrayList<>();
        for (LineResponse responseEntity : responseEntities) {
            stationNames.addAll(
                    responseEntity.getStations().stream().map(StationResponse::getName).collect(Collectors.toList()));
        }
        assertThat(stationNames).containsExactly("지하철역1", "지하철역2", "지하철역3", "지하철역4");

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
        long upStationId = createStation("서울역").getId();
        long downStationId = createStation("시청").getId();
        ExtractableResponse<Response> createResponse = createLineAndReturnResponse(
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
        assertThat(response.body().jsonPath().getString("message")).isNotBlank();
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
     * */
    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLine() {
        // given
        long id1 = createStation("지하철역1").getId();
        long id2 = createStation("지하철역2").getId();
        String createdLinePath = createLineAndReturnResponse("노선", "색깔", id1, id2, 10)
                .header("Location");

        // when
        LineUpdateRequest updateRequest = new LineUpdateRequest("changedName", "changedColor");
        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(createdLinePath)
                .then().log().all()
                .extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
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
        assertThat(response.body().jsonPath().getString("message")).isNotBlank();
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
        assertThat(response.body().jsonPath().getString("message")).isNotBlank();
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    void updateNonExistLine() {
        // given
        LineUpdateRequest updateRequest = new LineUpdateRequest("2호선", "파란색");

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
        long id1 = createStation("서울역").getId();
        long id2 = createStation("신설동").getId();
        String createdLinePath = createLineAndReturnResponse("1호선", "파란색", id1, id2, 10).header("Location");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(createdLinePath)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /*
     * given
     * station1, station2를 상행종점, 하행종점으로 하는 line1이 생성되어 있다.
     *
     * when
     * station2를 상행, station3을 하행으로 하는 거리 5짜리 구간을 생성한다.
     *
     * then
     * 성공 응답을 반환한다.
     * */
    @DisplayName("구간을 추가한다")
    @Test
    void addSection() {
        // given
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();
        String createdLinePath = createLineAndReturnResponse("line", "color", id1, id2, 10).header("Location");

        long id3 = createStation("station3").getId();
        SectionRequest sectionRequest = new SectionRequest(id2, id3, 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().log().all()
                .post(createdLinePath + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        long id1 = createStation("station1").getId();
        long id2 = createStation("station2").getId();
        String createdLinePath = createLineAndReturnResponse("line", "color", id1, id2, 10).header("Location");

        long id3 = createStation("station3").getId();
        SectionRequest sectionRequest = new SectionRequest(id2, id3, 5);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when()
                .post(createdLinePath + "/sections")
                .then()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().log().all().queryParam("stationId", id2)
                .delete(createdLinePath + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
