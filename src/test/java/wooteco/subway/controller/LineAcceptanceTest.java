package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.domain.station.Station;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() {
        //given
        Station station1 = createTestStation("createLine1역");
        Station station2 = createTestStation("createLine2역");

        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "00선");
        params.put("upStationId", String.valueOf(station1.getId()));
        params.put("downStationId", String.valueOf(station2.getId()));
        params.put("distance", "2");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("00선");
    }

    @DisplayName("지하철 노선 생성 예외 - 지하철 노선 생성시 입력 값이 제대로 들어오지 않으면 BadRequest를 던진다.")
    @Test
    void createLine_validation() {
        // given
        Station station1 = createTestStation("createLineValidation1역");
        Station station2 = createTestStation("createLineValidation2역");

        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", "");
        params.put("downStationId", String.valueOf(station2.getId()));
        params.put("distance", "");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        Station station1 = createTestStation("getLines1역");
        Station station2 = createTestStation("getLines2역");

        Map<String, String> param1 = new HashMap<>();
        param1.put("name", "2호선");
        param1.put("color", "bg-red-600");
        param1.put("upStationId", String.valueOf(station1.getId()));
        param1.put("downStationId", String.valueOf(station2.getId()));
        param1.put("distance", "4");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(param1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        Map<String, String> param2 = new HashMap<>();
        param2.put("name", "3호선");
        param2.put("color", "bg-red-600");
        param2.put("upStationId", String.valueOf(station1.getId()));
        param2.put("downStationId", String.valueOf(station2.getId()));
        param2.put("distance", "3");
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(param2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

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
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void showLine() {
        // given
        Station station1 = createTestStation("showLine1역");
        Station station2 = createTestStation("showLine2역");

        Map<String, String> param1 = new HashMap<>();
        param1.put("name", "4호선");
        param1.put("color", "bg-blue-600");
        param1.put("upStationId", String.valueOf(station1.getId()));
        param1.put("downStationId", String.valueOf(station2.getId()));
        param1.put("distance", "4");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(param1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", createResponse1.header("Location").split("/")[2])
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().get("name").toString()).isEqualTo("4호선");
        assertThat(response.body().jsonPath().get("color").toString()).isEqualTo("bg-blue-600");
        assertThat(response.body().jsonPath().getList("stations", StationResponse.class))
            .extracting("id", "name")
            .containsExactlyInAnyOrder(
                Tuple.tuple(station1.getId(), station1.getName()),
                Tuple.tuple(station2.getId(), station2.getName())
            );
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void updateLine() {
        // given
        Station station1 = createTestStation("updateLine1역");
        Station station2 = createTestStation("updateLine2역");

        Map<String, String> param1 = new HashMap<>();
        param1.put("name", "02호선");
        param1.put("color", "bg-blue-600");
        param1.put("upStationId", String.valueOf(station1.getId()));
        param1.put("downStationId", String.valueOf(station2.getId()));
        param1.put("distance", "4");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(param1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "03호선");
        updateParam.put("color", "bg-blue-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}", createResponse1.header("Location").split("/")[2])
            .then().log().all()
            .extract();

        ExtractableResponse<Response> expectedResponse = RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", createResponse1.header("Location").split("/")[2])
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(expectedResponse.body().jsonPath().get("name").toString())
            .isEqualTo(updateParam.get("name"));
        assertThat(expectedResponse.body().jsonPath().get("color").toString())
            .isEqualTo(updateParam.get("color"));
    }

    @DisplayName("지하철 노선 수정 예외 - 지하철 노선 수정시 입력 값이 제대로 들어오지 않으면 BadRequest를 던진다.")
    @Test
    void updateLine_validation() {
        // given
        Station station1 = createTestStation("updateLineValidation1역");
        Station station2 = createTestStation("updateLineValidation2역");

        Map<String, String> param1 = new HashMap<>();
        param1.put("name", "5호선");
        param1.put("color", "bg-blue-600");
        param1.put("upStationId", String.valueOf(station1.getId()));
        param1.put("downStationId", String.valueOf(station2.getId()));
        param1.put("distance", "4");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(param1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "");
        updateParam.put("color", "bg-blue-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}", createResponse1.header("Location").split("/")[2])
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        Station station1 = createTestStation("deleteLine1역");
        Station station2 = createTestStation("deleteLine2역");

        Map<String, String> param1 = new HashMap<>();
        param1.put("name", "7호선");
        param1.put("color", "bg-yellow-600");
        param1.put("upStationId", String.valueOf(station1.getId()));
        param1.put("downStationId", String.valueOf(station2.getId()));
        param1.put("distance", "4");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(param1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/{id}", createResponse1.header("Location").split("/")[2])
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Station createTestStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all().extract();
        Long id = response.body().jsonPath().getLong("id");
        return new Station(id, name);
    }
}
