package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.LineAndStationRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void set() {
        StationRequest upStationRequest = new StationRequest("강남역");
        StationAcceptanceTest.postStations(upStationRequest);
        StationRequest downStationRequest = new StationRequest("선릉역");
        StationAcceptanceTest.postStations(downStationRequest);
    }

    public static ExtractableResponse<Response> postLines(LineAndStationRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @Test
    @DisplayName("노선을 생성한다.")
    void createLine() {
        // given
        LineAndStationRequest request = new LineAndStationRequest("신분당선", "bg-red-600", 1L, 2L, 5);

        // when
        ExtractableResponse<Response> response = postLines(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("모든 노선을 조회한다.")
    void findAllLine() {
        //given
        LineAndStationRequest request1 = new LineAndStationRequest("신분당선", "bg-red-600", 1L, 2L, 5);
        ExtractableResponse<Response> createResponse1 = postLines(request1);

        LineAndStationRequest request2 = new LineAndStationRequest("분당선", "bg-green-600", 1L, 2L, 5);
        ExtractableResponse<Response> createResponse2 = postLines(request2);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("id별 노선을 조회한다.")
    void findLindById() {
        //given
        LineAndStationRequest request = new LineAndStationRequest("신분당선", "bg-red-600", 1L, 2L, 5);

        postLines(request);

        //when, then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 수정한다.")
    void update() {
        //given
        LineAndStationRequest request = new LineAndStationRequest("신분당선", "bg-red-600", 1L, 2L, 5);

        postLines(request);

        //when, then
        LineAndStationRequest changeRequest = new LineAndStationRequest("1호선", "bg-red-600", 1L, 2L, 5);

        RestAssured.given().log().all()
                .body(changeRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 삭제한다.")
    void delete() {
        //given
        LineAndStationRequest request = new LineAndStationRequest("신분당선", "bg-red-600", 1L, 2L, 5);

        postLines(request);

        //when, then
        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/1")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
