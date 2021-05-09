package wooteco.subway.line.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 테스트")
class LineAcceptanceTest extends AcceptanceTest {
    private static final String TEST_LINE_NAME = "2호선";
    private static final String TEST_COLOR_NAME = "orange darken-4";

    private static final LineRequest LINE_REQUEST = new LineRequest(TEST_LINE_NAME, TEST_COLOR_NAME, 1L, 2L, 10);

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void set() {
        stationDao.save(new Station(1L, "강남역"));
        stationDao.save(new Station(2L, "역삼역"));
        stationDao.save(new Station(3L, "잠실역"));
        stationDao.save(new Station(4L, "구의역"));
    }

    @AfterEach
    void cleanDB() {
        lineDao.deleteAll();
        stationDao.deleteAll();
    }

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(LINE_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        RestAssured.given().log().all()
                .body(LINE_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        LineRequest duplicateNameRequest = new LineRequest(TEST_LINE_NAME, "dark", 3L, 4L, 5);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(duplicateNameRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철노선 색상으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        RestAssured.given().log().all()
                .body(LINE_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        LineRequest duplicateColorRequest = new LineRequest("3호선", TEST_COLOR_NAME, 3L, 4L, 5);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(duplicateColorRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선을 id로 조회한다")
    @Test
    void getLineById() {
        /// given
        final ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(LINE_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo(TEST_LINE_NAME);
        assertThat(lineResponse.getColor()).isEqualTo(TEST_COLOR_NAME);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(LINE_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        LineRequest lineRequest2 = new LineRequest("3호선", "dark", 3L, 4L, 5);

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(lineRequest2)
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

    @DisplayName("노선 정보를 업데이트한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(LINE_REQUEST)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        Map<String, String> updateParam = new HashMap<>();
        String updateName = "빨리빨리노선";
        String updateColor = "red darken-3";

        updateParam.put("name", updateName);
        updateParam.put("color", updateColor);

        String uri = response.header("Location");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(updateParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        LineResponse result = updateResponse.body().as(LineResponse.class);

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getName()).isEqualTo(updateName);
        assertThat(result.getColor()).isEqualTo(updateColor);
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(LINE_REQUEST)
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

    @DisplayName("없는 ID의 지하철노선을 삭제하려고 하면 예외")
    @Test
    void whenTryDeleteWrongIdLine() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("lines/-1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}