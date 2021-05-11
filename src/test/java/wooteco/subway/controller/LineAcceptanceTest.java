package wooteco.subway.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.RequestUtil;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LineAcceptanceTest extends AcceptanceTest {
    private String location;
    private String namSungStationId;
    private String naeBangStationId;

    @BeforeEach
    void setUp7Line() {
        namSungStationId = RequestUtil.requestCreateStation("남성역").header("Location").split("/")[2];
        naeBangStationId = RequestUtil.requestCreateStation("내방역").header("Location").split("/")[2];
        int distance = 10;

        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-green-600");
        params.put("name", "7호선");
        params.put("upStationId", namSungStationId);
        params.put("downStationId", naeBangStationId);
        params.put("distance", Integer.toString(distance));

        ExtractableResponse<Response> createResponse = RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();

        location = createResponse.header("Location");
    }

    @AfterEach
    void stash() {
        RestAssured.given().log().all()
                .when()
                .delete(location)
                .then()
                .extract();
    }

    @Test
    @DisplayName("노선을 생성하는 요청을 보낸다.")
    void createLineTest() {
        // when
        ExtractableResponse<Response> response = RequestUtil.requestCreateLine("신분당선", "red", "판교역", "강남역", "23");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성하려하면 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // when
        ExtractableResponse<Response> response = RequestUtil.requestCreateLine("7호선", "red", "판교역", "강남역", "23");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선 목록을 조회하는 요청을 보낸다.")
    void showLines() {
        // given
        ExtractableResponse<Response> createResponse1 = RequestUtil.requestCreateLine("신분당선", "red", "강남역", "판교역", "24");

        ExtractableResponse<Response> createResponse2 = RequestUtil.requestCreateLine("2호선", "green", "홍대역", "잠실역", "30");

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
    @DisplayName("노선을 조회하는 요청을 보낸다.")
    void getLineTest() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(location)
                .then().log().all()
                .extract();

        String name = response.jsonPath().get("name");
        String color = response.jsonPath().get("color");

        List<StationResponse> stationResponses = response.jsonPath().getList("stations", StationResponse.class);
        List<StationResponse> expectedStationResponses = Arrays.asList(
                new StationResponse(Long.parseLong(namSungStationId), "남성역"),
                new StationResponse(Long.parseLong(naeBangStationId), "내방역")
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(name).isEqualTo("7호선");
        assertThat(color).isEqualTo("bg-green-600");
        assertThat(stationResponses).isEqualTo(expectedStationResponses);
    }

    @Test
    @DisplayName("노선을 수정하는 요청을 보낸다.")
    void updateLine() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(location)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(location)
                .then().log().all()
                .extract();

        String name = response.jsonPath().get("name");
        String color = response.jsonPath().get("color");

        // then
        assertThat(name).isEqualTo("구분당선");
        assertThat(color).isEqualTo("bg-blue-600");
    }

    @Test
    @DisplayName("노선을 삭제하는 요청을 보낸다.")
    void deleteLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(location)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}