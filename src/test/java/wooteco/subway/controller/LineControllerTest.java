package wooteco.subway.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @DisplayName("비어있는 이름으로 역을 생성하면 400 상태코드를 받게 된다.")
    @Test
    void createLineWithInvalidNameDateSize() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "");
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

    @DisplayName("비어있는 색으로 역을 생성하면 400 상태코드를 받게 된다.")
    @Test
    void createLineWithInvalidColorDateSize() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중복된 이름을 가진 지하철 노선을 등록할 때 예외를 발생시킨다.")
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

        Line updatedLine = lineDao.findById(line.getId());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedLine.getName()).isEqualTo("다른분당선"),
                () -> assertThat(updatedLine.getColor()).isEqualTo("blue")
        );
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

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThatThrownBy(() -> lineDao.findById(line.getId()))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessageMatching("id에 맞는 지하철 노선이 없습니다.")
        );
    }
}
