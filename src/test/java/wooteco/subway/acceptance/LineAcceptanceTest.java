package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
import wooteco.subway.dto.LineResponse;

class LineAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> createResponse;
    private Map<String, Object> createParams;

    @BeforeEach
    void setUpLine() {
        createStations();

        createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "red");
        createParams.put("upStationId", 1);
        createParams.put("downStationId", 2);
        createParams.put("distance", 10);

        createResponse = createLine(createParams);
    }

    private void createStations() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "사당역");
        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        params.put("name", "강남역");
        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLine(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine_created() {
        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createResponse.header("Location")).isNotBlank();
    }

    @DisplayName("이미 존재하는 노선 이름 혹은 노선 색으로 생성하면 bad Request를 응답한다.")
    @Test
    void createLine_badRequest() {
        // when
        ExtractableResponse<Response> response2 = createLine(createParams);

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록을 반환한다.")
    @Test
    void findAllLines() {
        //given
        createParams.put("name", "4호선");
        createParams.put("color", "skyBlue");
        createParams.put("upStationId", 1);
        createParams.put("downStationId", 2);
        createParams.put("distance", 10);

        ExtractableResponse<Response> createResponse2 = createLine(createParams);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선 아이디에 해당하는 노선을 반환한다.")
    @Test
    void findLine() {
        //given
        Long resultLineId = createResponse.jsonPath()
                .getObject(".", LineResponse.class)
                .getId();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선 아이디에 해당하는 노선을 수정한다.")
    @Test
    void updateLine() {
        Long resultLineId = createResponse.jsonPath()
                .getObject(".", LineResponse.class)
                .getId();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "5호선");
        params2.put("color", "skyBlue");

        //when
        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine_success() {
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

    @DisplayName("존재하지 않는 노선을 삭제하려고 하면 Bad Request를 응답한다.")
    @Test
    void deleteLine_badRequest() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/-1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
