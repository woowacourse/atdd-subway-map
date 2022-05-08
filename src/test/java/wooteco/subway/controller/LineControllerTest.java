package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineControllerTest {

    private LineRequest testLine1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10L);
    private LineRequest testLine2 = new LineRequest("분당선", "bg-red-600", 3L, 4L, 20L);
    private LineRequest testLine3 = new LineRequest("2호선", "bg-green-500", 5L, 6L, 30L);

    @LocalServerPort
    int port;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        StationDao stationDao = new StationDao(jdbcTemplate);

        stationDao.save(new Station(1L, "testStation1"));
        stationDao.save(new Station(2L, "testStation2"));
        stationDao.save(new Station(3L, "testStation3"));
        stationDao.save(new Station(4L, "testStation4"));
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = RestAssured.
                given().log().all()
                    .body(testLine1)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).
                when()
                    .post("/lines").
                then()
                    .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        RestAssured.
                given().log().all()
                    .body(testLine1)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).
                when()
                    .post("/lines").
                then()
                    .extract();

        // when
        RestAssured.
                given().log().all()
                    .body(testLine1)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        ExtractableResponse<Response> createResponse = RestAssured.
                given().log().all()
                    .body(testLine1)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).
                when()
                    .post("/lines").
                then().
                    extract();

        // when
        String uri = createResponse.header("Location");
        RestAssured.
                given().log().all().
                when()
                    .get(uri).
                then().
                    statusCode(HttpStatus.OK.value());
    }

    @DisplayName("노선들을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.
                given().log().all()
                    .body(testLine1)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).
                when()
                    .post("/lines").
                then()
                    .extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.
                given().log().all()
                    .body(testLine2)
                    .contentType(MediaType.APPLICATION_JSON_VALUE).
                when()
                    .post("/lines").
                then().
                    extract();

        // when
        ExtractableResponse<Response> response = RestAssured.
                given().
                    log().all().
                when().
                    get("/lines")
                .then().
                    extract();

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

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.
                given().log().all().
                    body(testLine1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then().
                    extract();

        // when
        String uri = createResponse.header("Location");
        RestAssured.given().log().all().
                when().
                    delete(uri).
                then().
                    statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하는 id와 중복되지 않는 이름으로 바꾼다.")
    @Test
    void change_name_success() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.
                given().log().all().
                    body(testLine1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then()
                    .extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.
                given().
                    log().all()
                    .body(testLine2).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then().
                     extract();

        // when
        String uri = createResponse1.header("Location");
        RestAssured.
                given().log().all().
                    body(testLine3).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    put(uri).
                then().
                    statusCode(HttpStatus.OK.value()).log().all();
    }

    @DisplayName("존재하지 않는 id의 노선 이름을 변경한다.")
    @Test
    void change_name_no_id() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.
                given().log().all().
                    body(testLine1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then()
                    .extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.
                given().log().all().
                    body(testLine2).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then().
                    extract();

        // when
        String uri = "/lines/1000";
        RestAssured.
                given().log().all().
                    body(testLine3).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    put(uri).
                then().
                    statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("이미 저장된 이름으로 이름을 바꾼다.")
    @Test
    void change_name_name_duplicate() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.
                given().log().all().
                    body(testLine1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then().
                    extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.
                given().log().all().
                    body(testLine2).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then().
                    extract();

        // when
        String uri = createResponse1.header("Location");
        RestAssured.
                given().log().all().
                    body(testLine2).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    put(uri).
                then().
                    statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
