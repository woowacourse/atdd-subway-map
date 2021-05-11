package wooteco.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.AcceptanceTest;

@Transactional
@Sql("classpath:test-schema.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUpStations() {
        addStation("강남역");
        addStation("성수역");

        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/stations")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> response = addLine("테스트선", "red", 1, 2, 1000, 100);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("잘못된 역 id로 지하철 노선을 생성한다.")
    @Test
    void createLineException() {
        // given
        ExtractableResponse<Response> response = addLine("테스트선", "red", 1, 3, 1000, 100);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void showLines() {
        // given
        ExtractableResponse<Response> response = addLine("테스트선", "red", 1, 2, 1000, 100);

        //when
        ExtractableResponse<Response> getLinesResponse = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        //then
        assertThat(getLinesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(response)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());

        List<Long> resultLineIds = getLinesResponse.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선을 조회한다")
    @Test
    void showLine() {
        //given
        addLine("테스트선", "red", 1, 2, 1000, 100);

        //when
        ExtractableResponse<Response> getLineResponse = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/1")
            .then().log().all()
            .extract();

        //then
        LineResponse lineResponse = getLineResponse.jsonPath().getObject(".", LineResponse.class);

        assertThat(getLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo("테스트선");
        assertThat(lineResponse.getColor()).isEqualTo("red");
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        addLine("테스트선", "red", 1, 2, 1000, 100);

        //when
        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("name", "수정한선");
        requestParam.put("color", "blue");

        ExtractableResponse<Response> putLineResponse = RestAssured.given().log().all()
            .body(requestParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/1")
            .then().log().all()
            .extract();

        //then
        assertThat(putLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteLine() {
        //given
        addLine("테스트선", "red", 1, 2, 1000, 100);

        //when
        ExtractableResponse<Response> deleteLineResponse = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/1")
            .then().log().all()
            .extract();

        //then
        assertThat(deleteLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void addStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> addLine(String name, String color, long upStationId, long downStationId,
        int distance, int extraFare) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        params.put("extraFare", extraFare);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }
}
