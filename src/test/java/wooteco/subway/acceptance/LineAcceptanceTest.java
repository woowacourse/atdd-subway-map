package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.StationAcceptanceTest.postStations;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.line.LineSaveRequest;
import wooteco.subway.dto.line.LineUpdateRequest;
import wooteco.subway.dto.station.StationResponse;
import wooteco.subway.dto.station.StationSaveRequest;

class LineAcceptanceTest extends AcceptanceTest {

    public static ExtractableResponse<Response> postLines(final LineSaveRequest lineSaveRequest) {
        return RestAssured.given().log().all()
                .body(lineSaveRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLine(final String location) {
        return RestAssured.given().log().all()
                .when()
                .get(location)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> putLine(final String location, final LineUpdateRequest lineUpdateRequest) {
        return RestAssured.given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(location)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> deleteLine(final String location) {
        return RestAssured.given().log().all()
                .when()
                .delete(location)
                .then().log().all()
                .extract();
    }

    @Test
    @DisplayName("노선을 추가한다.")
    void save() {
        //given
        Long upStationId = postStations(new StationSaveRequest("강남역")).response()
                .as(StationResponse.class)
                .getId();
        Long downStationId = postStations(new StationSaveRequest("역삼역")).response()
                .as(StationResponse.class)
                .getId();
        LineSaveRequest lineSaveRequest = new LineSaveRequest("신분당선", "bg-red-600", upStationId, downStationId, 3);

        //when
        ExtractableResponse<Response> response = postLines(lineSaveRequest);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @Test
    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성하면 노선을 생성할 수 없다.")
    void createLinesWithExistNames() {
        // given
        Long upStationId = postStations(new StationSaveRequest("강남역")).response()
                .as(StationResponse.class)
                .getId();
        Long downStationId = postStations(new StationSaveRequest("역삼역")).response()
                .as(StationResponse.class)
                .getId();
        LineSaveRequest lineSaveRequest = new LineSaveRequest("신분당선", "bg-red-600", upStationId, downStationId, 3);
        postLines(lineSaveRequest);

        // when
        ExtractableResponse<Response> response = postLines(lineSaveRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("전체 노선 목록을 조회한다.")
    void findAllLines() {
        //given
        Long upStationId = postStations(new StationSaveRequest("강남역")).response()
                .as(StationResponse.class)
                .getId();
        Long downStationId = postStations(new StationSaveRequest("역삼역")).response()
                .as(StationResponse.class)
                .getId();
        postLines(new LineSaveRequest("신분당선", "bg-red-600", upStationId, downStationId, 3));
        postLines(new LineSaveRequest("분당선", "bg-green-600", upStationId, downStationId, 3));

        // when
        ExtractableResponse<Response> response = getLines();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList(".", LineResponse.class)).hasSize(2)
        );
    }

    @Test
    @DisplayName("단일 노선을 조회한다.")
    void findLine() {
        // given
        Long upStationId = postStations(new StationSaveRequest("강남역")).response()
                .as(StationResponse.class)
                .getId();
        Long downStationId = postStations(new StationSaveRequest("역삼역")).response()
                .as(StationResponse.class)
                .getId();
        String location = postLines(new LineSaveRequest("신분당선", "bg-red-600", upStationId, downStationId, 3))
                .header("Location");

        // when
        ExtractableResponse<Response> response = getLine(location);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void updateLine() {
        //given
        Long upStationId = postStations(new StationSaveRequest("강남역")).response()
                .as(StationResponse.class)
                .getId();
        Long downStationId = postStations(new StationSaveRequest("역삼역")).response()
                .as(StationResponse.class)
                .getId();
        String location = postLines(new LineSaveRequest("신분당선", "bg-red-600", upStationId, downStationId, 3))
                .header("Location");

        // when
        ExtractableResponse<Response> response = putLine(location, new LineUpdateRequest("분당선", "bg-green-600"));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void deleteLine() {
        //given
        Long upStationId = postStations(new StationSaveRequest("강남역")).response()
                .as(StationResponse.class)
                .getId();
        Long downStationId = postStations(new StationSaveRequest("역삼역")).response()
                .as(StationResponse.class)
                .getId();
        String location = postLines(new LineSaveRequest("신분당선", "bg-red-600", upStationId, downStationId, 3))
                .header("Location");

        // when
        ExtractableResponse<Response> response = deleteLine(location);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
