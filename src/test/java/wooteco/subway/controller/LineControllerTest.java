package wooteco.subway.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class LineControllerTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;

    @DisplayName("지하철 노선을 등록한다.")
    @Test
    void createLine() {
        LineRequest request = new LineRequest("신분당선", "red", null, null, 0);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
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
        LineRequest request = new LineRequest("", "red", null, null, 0);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
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
        LineRequest request = new LineRequest("신분당선", "", null, null, 0);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
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
        LineRequest request = new LineRequest("신분당선", "red", null, null, 0);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(request)
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
        Line savedLine1 = lineDao.save(new Line("신분당선", "red"));
        Line savedLine2 = lineDao.save(new Line("1호선", "blue"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        List<LineResponse> actual = response.jsonPath().getList(".", LineResponse.class);

        assertAll(
                () -> assertThat(actual.get(0).getId()).isEqualTo(savedLine1.getId()),
                () -> assertThat(actual.get(0).getName()).isEqualTo(savedLine1.getName()),
                () -> assertThat(actual.get(0).getColor()).isEqualTo(savedLine1.getColor()),

                () -> assertThat(actual.get(1).getId()).isEqualTo(savedLine2.getId()),
                () -> assertThat(actual.get(1).getName()).isEqualTo(savedLine2.getName()),
                () -> assertThat(actual.get(1).getColor()).isEqualTo(savedLine2.getColor())
        );
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
        Line savedLine = lineDao.save(new Line("신분당선", "red"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + savedLine.getId())
                .then().log().all()
                .extract();

        List<Line> lines = lineDao.findAll();

        assertThat(lines.contains(savedLine)).isFalse();
    }
}
