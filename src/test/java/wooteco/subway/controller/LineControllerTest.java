package wooteco.subway.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
class LineControllerTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;

    @DisplayName("지하철 노선을 등록한다.")
    @Test
    void createLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("중복된 이름을 가진 지하철 노선을 등록할 때 400 상태코드로 응답한다.")
    @Test
    void throwsExceptionWhenCreateDuplicatedName() {
        lineDao.save(new Line("신분당선", "red"));

        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        lineDao.save(new Line("신분당선", "red"));
        lineDao.save(new Line("1호선", "blue"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        List<LineResponse> actual = response.jsonPath().getList(".", LineResponse.class);

        assertThat(actual.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        Line line = lineDao.save(new Line("신분당선", "red"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + line.getId())
                .then().log().all()
                .extract();

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo(line.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(line.getColor())
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        Line line = lineDao.save(new Line("신분당선", "red"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(new Line("다른분당선", "blue"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + line.getId())
                .then().log().all()
                .extract();

        Line updatedLine = lineDao.findById(line.getId()).get();
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedLine.getName()).isEqualTo("다른분당선"),
                () -> assertThat(updatedLine.getColor()).isEqualTo("blue")
        );
    }

    @DisplayName("노선에서 수정하려는 이름을 가진 노선이 존재한다면 400 상태코드로 응답한다.")
    @Test
    void updateLineResponse400() {
        lineDao.save(new Line("다른분당선", "blue"));
        Line line = lineDao.save(new Line("신분당선", "red"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(new Line("다른분당선", "blue"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + line.getId())
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정할 때 id에 맞는 노선이 없으면 404 상태코드로 응답한다.")
    @Test
    void updateLineResponse404() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(new Line("다른분당선", "blue"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + 1)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        Line line = lineDao.save(new Line("신분당선", "red"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + line.getId())
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선을 삭제할 때 id에 맞는 노선이 없으면 404 상태코드로 응답한다.")
    @Test
    void deleteLineResponse404() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + 1L)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
