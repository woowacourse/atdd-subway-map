package wooteco.subway.ui;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.acceptance.AcceptanceTest;
import wooteco.subway.assembler.Assembler;
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

    @AfterEach
    void afterEach() {
        Assembler.getLineDao().clear();
    }

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
        assertThat(response.header("Location")).isEqualTo("/lines/1");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        Assembler.getLineDao().save(new Line("신분당선", "red"));
        Assembler.getLineDao().save(new Line("1호선", "blue"));

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
        Line line = Assembler.getLineDao().save(new Line("신분당선", "red"));

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
        Line line = Assembler.getLineDao().save(new Line("신분당선", "red"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(new Line("다른분당선", "blue"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + line.getId())
                .then().log().all()
                .extract();

        Line updatedLine = Assembler.getLineDao().findById(line.getId());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(updatedLine.getName()).isEqualTo("다른분당선"),
                () -> assertThat(updatedLine.getColor()).isEqualTo("blue")
        );
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        Line line = Assembler.getLineDao().save(new Line("신분당선", "red"));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + line.getId())
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThatThrownBy(() -> Assembler.getLineDao().findById(line.getId()))
                        .isInstanceOf(NotFoundException.class)
                        .hasMessageMatching("id에 맞는 지하철 노선이 없습니다.")
        );
    }
}
