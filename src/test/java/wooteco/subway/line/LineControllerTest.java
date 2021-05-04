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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class LineControllerTest extends AcceptanceTest {
    private ExtractableResponse<Response> response;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        RestAssured.given().delete("/lines");

        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        response = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @DisplayName("노선 추가하는데 성공하면 201 created와 생성된 노선 정보를 반환한다")
    @Test
    void createLine() {
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        assertThat(response.response().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.response().jsonPath().getLong("id")).isEqualTo(1L);
        assertThat(response.as(LineResponse.class).getName()).isEqualTo("신분당선");
    }

    @DisplayName("전체 노선을 조회하면 저장된 모든 노선들을 반환한다 ")
    @Test
    void getLines() {
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-green-600");
        params1.put("name", "2호선");
        RestAssured.given().log().all()
                .body(params1)
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

        Map<String, Object> sinBunDangLine = (Map<String, Object>) response.as(ArrayList.class).get(0);
        Map<String, Object> line2 = (Map<String, Object>) response.as(ArrayList.class).get(1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(sinBunDangLine.get("name")).isEqualTo("신분당선");
        assertThat(line2.get("name")).isEqualTo("2호선");
    }

    @DisplayName("id를 통해 노선을 조회하면, 해당 노선 정보를 반환한다.")
    @Test
    void getLine() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        LineResponse expectedLineResponse = new LineResponse(
                1L,
                "신분당선",
                "bg-red-600"
        );
        assertThat(getResponse.as(LineResponse.class)).usingRecursiveComparison().isEqualTo(expectedLineResponse);
    }

    @DisplayName("id를 통해 노선을 변경하면, payload대로 노선 수정한다")
    @Test
    void updateLine() {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");

        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("id를 통해 노선을 삭제하면, payload대로 노선을 삭제한다")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/1")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}