package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "grey darken-1");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 2);
        params.put("extraFare", 500);

        // when
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
        assertThat(response.body().as(LineResponse.class).getId())
            .isEqualTo(Long.parseLong(response.header("Location").split("/")[2]));
        assertThat(response.body().as(LineResponse.class).getName()).isEqualTo("2호선");
        assertThat(response.body().as(LineResponse.class).getColor()).isEqualTo("grey darken-1");
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, Object> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "grey darken-1");
        params1.put("upStationId", 1);
        params1.put("downStationId", 2);
        params1.put("distance", 2);
        params1.put("extraFare", 500);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void showLines() {
        /// given
        Map<String, Object> params1 = new HashMap<>();
        params1.put("name", "2호선");
        params1.put("color", "grey darken-1");
        params1.put("upStationId", 1);
        params1.put("downStationId", 2);
        params1.put("distance", 2);
        params1.put("extraFare", 500);

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "3호선");
        params2.put("color", "grey darken-2");
        params2.put("upStationId", 5);
        params2.put("downStationId", 6);
        params2.put("distance", 12);
        params2.put("extraFare", 1500);
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
            .body(params2)
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
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "grey darken-1");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 2);
        params.put("extraFare", 500);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        Long createId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + createId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(LineResponse.class).getId()).isEqualTo(createId);
        assertThat(response.body().as(LineResponse.class).getName()).isEqualTo("2호선");
        assertThat(response.body().as(LineResponse.class).getColor()).isEqualTo("grey darken-1");
    }

    @DisplayName("없는 노선을 조회한다.")
    @Test
    void showNotExistLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/2000000")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "grey darken-1");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 2);
        params.put("extraFare", 500);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        Map<String, Object> updatedParams = new HashMap<>();
        updatedParams.put("name", "3호선");
        updatedParams.put("color", "grey darken-2");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updatedParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("없는 노선을 수정한다.")
    @Test
    void updateNotExistLine() {
        Map<String, Object> updatedParams = new HashMap<>();
        updatedParams.put("name", "3호선");
        updatedParams.put("color", "grey darken-2");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updatedParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/2000000")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "grey darken-1");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 2);
        params.put("extraFare", 500);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
            .body(params)
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

    @DisplayName("없는 노선을 제거한다.")
    @Test
    void deleteNotExistLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/2000000")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
