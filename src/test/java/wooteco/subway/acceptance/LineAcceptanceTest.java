package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.ExceptionResponse;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine_created() {
        //given
        requestToCreateStation("강남역");
        requestToCreateStation("사당역");

        //when
        ExtractableResponse<Response> response =
                requestToCreateLine("신분당선", "red", "1", "2", "10");

        LineResponse lineResponse = response.jsonPath()
                .getObject(".", LineResponse.class);
        List<StationResponse> stations = lineResponse.getStations();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(lineResponse.getId()).isEqualTo(1L),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(stations.get(0).getId()).isEqualTo(1L),
                () -> assertThat(stations.get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(stations.get(1).getId()).isEqualTo(2L),
                () -> assertThat(stations.get(1).getName()).isEqualTo("사당역")
        );
    }

    @DisplayName("이미 존재하는 노선 이름 혹은 노선 색으로 생성하면 bad Request를 응답한다.")
    @Test
    void createLine_badRequest() {
        //given
        requestToCreateLine("신분당선", "red", "1", "2", "10");

        //when
        ExtractableResponse<Response> response =
                requestToCreateLine("신분당선", "red", "1", "2", "10");
        final ExceptionResponse exceptionResponse = response.jsonPath()
                .getObject(".", ExceptionResponse.class);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getExceptionMessage()).isEqualTo("노선 이름 혹은 노선 색이 이미 존재합니다.");
    }

    @DisplayName("노선 목록을 반환한다.")
    @Test
    void findAllLines() {
        //given
        requestToCreateStation("강남역");
        requestToCreateStation("사당역");
        requestToCreateStation("선릉역");
        requestToCreateLine("신분당선", "red", "1", "2", "10");
        requestToCreateLine("4호선", "skyBlue", "2", "3", "6");

        //when
        ExtractableResponse<Response> response = requestToFindAllLines();
        final List<LineResponse> responses = response.jsonPath()
                .getList(".", LineResponse.class);

        LineResponse firstLineResponse = responses.get(0);
        LineResponse secondLineResponse = responses.get(1);
        final StationResponse firstLineUpStation = firstLineResponse.getStations().get(0);
        final StationResponse firstLineDownStation = firstLineResponse.getStations().get(1);
        final StationResponse secondLineUpStation = secondLineResponse.getStations().get(0);
        final StationResponse secondLineDownStation = secondLineResponse.getStations().get(1);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(firstLineResponse.getId()).isEqualTo(1L),
                () -> assertThat(firstLineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(firstLineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(firstLineUpStation.getId()).isEqualTo(1L),
                () -> assertThat(firstLineUpStation.getName()).isEqualTo("강남역"),
                () -> assertThat(firstLineDownStation.getId()).isEqualTo(2L),
                () -> assertThat(firstLineDownStation.getName()).isEqualTo("사당역"),

                () -> assertThat(secondLineResponse.getId()).isEqualTo(2L),
                () -> assertThat(secondLineResponse.getName()).isEqualTo("4호선"),
                () -> assertThat(secondLineResponse.getColor()).isEqualTo("skyBlue"),
                () -> assertThat(secondLineUpStation.getId()).isEqualTo(2L),
                () -> assertThat(secondLineUpStation.getName()).isEqualTo("사당역"),
                () -> assertThat(secondLineDownStation.getId()).isEqualTo(3L),
                () -> assertThat(secondLineDownStation.getName()).isEqualTo("선릉역")
        );
    }

    @DisplayName("노선 아이디에 해당하는 노선을 반환한다.")
    @Test
    void findLine() {
        //given
        requestToCreateStation("강남역");
        requestToCreateStation("선릉역");

        ExtractableResponse<Response> createResponse =
                requestToCreateLine("신분당선", "red", "1", "2", "3");

        Long resultLineId = createResponse.jsonPath()
                .getObject(".", LineResponse.class)
                .getId();

        //when
        final ExtractableResponse<Response> response = requestToFindLineById(resultLineId);
        LineResponse lineResponse = response.jsonPath()
                .getObject(".", LineResponse.class);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getId()).isEqualTo(1L),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red")
        );
    }

    @DisplayName("노선 아이디에 해당하는 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        requestToCreateStation("강남역");
        requestToCreateStation("사당역");

        Long resultLineId =
                requestToCreateLine("신분당선", "red", "1", "2", "10")
                        .jsonPath()
                        .getObject(".", LineResponse.class)
                        .getId();

        //when
        ExtractableResponse<Response> response =
                requestToUpdateLine(resultLineId, "5호선", "skyBlue");

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine_success() {
        //given
        requestToCreateStation("강남역");
        requestToCreateStation("사당역");

        Long resultLineId =
                requestToCreateLine("신분당선", "red", "1", "2", "10")
                        .jsonPath()
                        .getObject(".", LineResponse.class)
                        .getId();
        //when
        final ExtractableResponse<Response> response = requestToDeleteLine(resultLineId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 노선을 삭제하려고 하면 Bad Request를 응답한다.")
    @Test
    void deleteLine_badRequest() {
        // when
        ExtractableResponse<Response> response = requestToFindLineById(-1L);
        final ExceptionResponse exceptionResponse = response.jsonPath()
                .getObject(".", ExceptionResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(exceptionResponse.getExceptionMessage()).isEqualTo("존재하지 않는 노선입니다.");
    }


    private void requestToCreateStation(String stationName) {
        RestAssured.given().log().all()
                .body(Map.of("name", stationName))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestToCreateLine(String name, String color, String upStationId,
                                                              String downStationId, String distance) {
        Map<String, String> params = Map.of(
                "name", name,
                "color", color,
                "upStationId", upStationId,
                "downStationId", downStationId,
                "distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestToFindAllLines() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestToFindLineById(Long lindId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lindId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestToUpdateLine(final Long resultLineId, String name, String color) {
        Map<String, Object> params = Map.of("name", name, "color", color);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestToDeleteLine(final Long lineId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then().log().all()
                .extract();
    }
}
