package wooteco.subway.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@Sql("/truncate.sql")
class LineControllerTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;

    @DisplayName("지하철 노선을 등록한다.")
    @ParameterizedTest
    @MethodSource("params")
    void createLine(String name, String color, Long upStationId, Long downStationId,
                                         int distance, int httpStatusCode) {
        stationDao.save(new Station("동천역"));
        stationDao.save(new Station("판교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(httpStatusCode);
    }

    private static Stream<Arguments> params() {
        return Stream.of(
                Arguments.of("신분당선", "red", 1L, 2L, 1, 201),
                Arguments.of("신분당선", " ", 1L, 2L, 1, 400),
                Arguments.of(" ", "red", 1L, 2L, 1, 400),
                Arguments.of("신분당선", "red", 2L, 3L, 1, 404),
                Arguments.of("신분당선", "red", 1L, 1L, 1, 400),
                Arguments.of("신분당선", "red", 1L, 2L, 0, 400)
        );
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        Station upStation = stationDao.save(new Station("동천역"));
        Station downStation = stationDao.save(new Station("판교역"));
        Line line = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(upStation.getId(), downStation.getId(), line.getId(), 5));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
        List<LineResponse> actual = response.jsonPath().getList(".", LineResponse.class);

        assertThat(actual.size()).isEqualTo(1);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        Station upStation = stationDao.save(new Station("동천역"));
        Station downStation = stationDao.save(new Station("판교역"));
        Line line = lineDao.save(new Line("신분당선", "red"));
        sectionDao.save(new Section(upStation.getId(), downStation.getId(), line.getId(), 5));

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + line.getId())
                .then().log().all()
                .extract();

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
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
