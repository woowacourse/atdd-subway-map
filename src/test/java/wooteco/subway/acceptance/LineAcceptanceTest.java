package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @AfterEach
    void tearDown() {
        List<Long> lineIds = LineDao.findAll().stream()
                .map(Line::getId)
                .collect(Collectors.toList());

        for (Long lineId : lineIds) {
            LineDao.deleteById(lineId);
        }
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("노션을 조회한다.")
    @Test
    void getLines() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");

        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "분당선");
        params2.put("color", "bg-green-600");

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
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("개별 노선을 ID 값으로 조회한다.")
    @Test
    void getLineById() {
        /// given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");

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
                .get("/lines/1")
                .then().log().all()
                .extract();

        // then
        String id = response.jsonPath().getString("id");
        String name = response.jsonPath().getString("name");
        String color = response.jsonPath().getString("color");

        assertAll(
                () -> assertThat(id).isEqualTo("1"),
                () -> assertThat(name).isEqualTo("신분당선"),
                () -> assertThat(color).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLineById() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Long createdId = createResponse.jsonPath().getLong("id");

        // when
        String name = "다른분당선";
        String color = "bg-red-600";

        Map<String, String> newParams = new HashMap<>();
        newParams.put("name", name);
        newParams.put("color", color);

        RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + createdId)
                .then().log().all()
                .extract();

        // then
        Line line = LineDao.findById(createdId);
        String responseName = line.getName();
        String responseColor = line.getColor();

        assertAll(
                () -> assertThat(responseName).isEqualTo(name),
                () -> assertThat(responseColor).isEqualTo(color)
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

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

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철역을 생성할 경우 예외를 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");

        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "신분당선");
        params2.put("color", "bg-red-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
