package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void init() {
        Map<String, String> station1 = new HashMap<>();
        station1.put("name", "강남역");
        RestAssured.given().log().all()
                .body(station1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        Map<String, String> station2 = new HashMap<>();
        station2.put("name", "선릉역");
        RestAssured.given().log().all()
                .body(station2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, Object> newLine = new HashMap<>();
        newLine.put("name", "1호선");
        newLine.put("color", "red");
        newLine.put("upStationId", 1);
        newLine.put("downStationId", 2);
        newLine.put("distance", 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, Object> newLine = new HashMap<>();
        newLine.put("name", "1호선");
        newLine.put("color", "red");
        newLine.put("upStationId", 1);
        newLine.put("downStationId", 2);
        newLine.put("distance", 10);

        RestAssured.given().log().all()
                .body(newLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, Object> line1 = new HashMap<>();
        line1.put("name", "1호선");
        line1.put("color", "red");
        line1.put("upStationId", 1);
        line1.put("downStationId", 2);
        line1.put("distance", 10);

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(line1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, Object> line2 = new HashMap<>();
        line2.put("name", "2호선");
        line2.put("color", "blue");
        line2.put("upStationId", 1);
        line2.put("downStationId", 2);
        line2.put("distance", 10);

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(line2)
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
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, Object> existedLine = new HashMap<>();
        existedLine.put("name", "1호선");
        existedLine.put("color", "red");
        existedLine.put("upStationId", 1);
        existedLine.put("downStationId", 2);
        existedLine.put("distance", 10);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(existedLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

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

    @DisplayName("노선을 조회한다.")
    @Test
    void searchLine() {
        // given
        Map<String, Object> existedLine = new HashMap<>();
        existedLine.put("name", "1호선");
        existedLine.put("color", "red");
        existedLine.put("upStationId", 1);
        existedLine.put("downStationId", 2);
        existedLine.put("distance", 10);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(existedLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        String expectedLine = createResponse.body().asString();
        String resultLine = response.body().asString();

        assertThat(resultLine).isEqualTo(expectedLine);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void editLine() {
        // given
        Map<String, Object> newLine = new HashMap<>();
        newLine.put("name", "1호선");
        newLine.put("color", "red");
        newLine.put("upStationId", 1);
        newLine.put("downStationId", 2);
        newLine.put("distance", 10);

        RestAssured.given().log().all()
                .body(newLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        Map<String, Object> editLine = new HashMap<>();
        editLine.put("name", "2호선");
        editLine.put("color", "blue");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(editLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
