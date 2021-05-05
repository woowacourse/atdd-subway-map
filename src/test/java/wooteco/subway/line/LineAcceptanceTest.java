package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void setUpStations() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // given
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "성수역");
        RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        params.put("extraFare", "100");

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선 목록을 조회한다")
    @Test
    void showLines() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        params.put("extraFare", "100");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        //when
        ExtractableResponse<Response> getLinesResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        //then
        assertThat(getLinesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Collections.singletonList(response).stream()
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
        Map<String, Object> params = new HashMap<>();
        params.put("name", "테스트선");
        params.put("color", "red");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", "1000");
        params.put("extraFare", "100");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

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
}
