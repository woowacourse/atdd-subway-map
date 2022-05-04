package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.LineDao;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void beforeEach() {
        LineDao.deleteAll();
    }

    @DisplayName("지하철 노선 이름에 빈 문자열을 사용할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void createLineWithEmptyName(String lineName) {
        RestAssured.given().log().all()
            .body(Map.of("name", lineName, "color", "bg-red-600"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 색깔에 빈 문자열을 사용할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void createLineWithEmptyColor(String color) {
        RestAssured.given().log().all()
            .body(Map.of("name", "신분당선", "color", color))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 등록")
    @Test
    void createLine() {
        String name = "신분당선";
        String color = "bg-red-600";

        RestAssured.given().log().all()
            .body(Map.of("name", name, "color", color))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .statusCode(HttpStatus.CREATED.value())
            .header("Location", not(emptyOrNullString()))
            .body("id", notNullValue())
            .body("name", equalTo(name))
            .body("color", equalTo(color));
    }

    @DisplayName("지하철 노선 중복 등록을 허용하지 않는다")
    @Test
    void createStationWithDuplicateName() {
        // given
        String name = "신분당선";
        String color = "bg-red-600";
        requestCreateLine(name, color);

        // when
        ExtractableResponse<Response> response = requestCreateLine(name, color);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> requestCreateLine(String lineName, String lineColor) {
        return RestAssured.given().log().all()
            .body(Map.of("name", lineName, "color", lineColor))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void showLineById() {
        // given
        String name = "신분당선";
        String color = "bg-red-600";
        ExtractableResponse<Response> createResponse = requestCreateLine(name, color);
        Long createdId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().get("/lines/" + createdId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getLong("id")).isNotNull();
        assertThat(response.jsonPath().getString("name")).isEqualTo(name);
        assertThat(response.jsonPath().getString("color")).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 지하철 노선 조회시 예외를 반환한다")
    @Test
    void showNotExistLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().get("/lines/" + 50L)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void showLines() {
        // given
        ExtractableResponse<Response> createResponse1 = requestCreateLine("신분당선", "bg-red-600");
        ExtractableResponse<Response> createResponse2 = requestCreateLine("1호선", "bg-blue-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();

        // then
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
            .map(it -> it.jsonPath().getObject("id", Long.class))
            .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList("id", Long.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(expectedLineIds).containsExactlyInAnyOrderElementsOf(actualLineIds);
    }

    @DisplayName("이름이 공백인 지하철 노선을 수정할 수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "     "})
    void canNotUpdateLineWithEmptyName(String lineName) {

        // given
        ExtractableResponse<Response> response = requestCreateLine("신분당선", "bg-red-600");
        long createdId = response.jsonPath().getLong("id");

        // when & then
        RestAssured.given().log().all()
                .body(Map.of("name", lineName, "color", "bg-red-600"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + createdId)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
