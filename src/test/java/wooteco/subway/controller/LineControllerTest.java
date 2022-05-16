package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.acceptance.AcceptanceTest;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@Sql("classpath:test-schema.sql")
class LineControllerTest extends AcceptanceTest {

    private LineRequest testLine1;
    private LineRequest testLine2;
    private LineRequest testLine3;

    @LocalServerPort
    int port;

    @Autowired
    private StationDao stationDao;

    private Station testStation1;
    private Station testStation2;
    private Station testStation3;
    private Station testStation4;
    private Station testStation5;
    private Station testStation6;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        testStation1 = stationDao.save(new Station("testStation1"));
        testStation2 = stationDao.save(new Station("testStation2"));
        testStation3 = stationDao.save(new Station("testStation3"));
        testStation4 = stationDao.save(new Station("testStation4"));
        testStation5 = stationDao.save(new Station("testStation5"));
        testStation6 = stationDao.save(new Station("testStation6"));

        testLine1 = new LineRequest("신분당선", "bg-red-600", testStation1.getId(), testStation2.getId(), 10L);
        testLine2  = new LineRequest("분당선", "bg-red-600", testStation3.getId(), testStation4.getId(), 20L);
        testLine3 = new LineRequest("2호선", "bg-green-500", testStation5.getId(), testStation6.getId(), 30L);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createStation() {
        // when
        RestAssured.
                given().log().all().
                    body(testLine1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then().log().all().
                    statusCode(HttpStatus.CREATED.value()).
                    header("Location", is(not("")));
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        RestAssured.
                given().log().all().
                    body(testLine1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then();

        // when && then
        RestAssured.
                given().log().all().
                    body(testLine1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then().
                    statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLine_success() {
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
        RestAssured.
                given().log().all().
                when().
                    get(uri).
                then().log().all().
                    statusCode(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는노선을 조회한다.")
    @Test
    void getLine_fail() {
        /// given
        RestAssured.
                given().log().all()
                .body(testLine1)
                .contentType(MediaType.APPLICATION_JSON_VALUE).
                        when()
                .post("/lines").
                        then().
                        extract();

        // when
        RestAssured.
                given().log().all().
                when()
                .get("/lines/{lineId}", -1).
                then().log().all().
                statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선들을 조회한다.")
    @Test
    void getStations() {
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
        ExtractableResponse<Response> response = RestAssured.
                given().log().all().
                when().
                    get("/lines").
                then().log().all().
                    statusCode(HttpStatus.OK.value()).
                    extract();

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
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
                then().log().all().
                    extract();

        // when
        String uri = createResponse.header("Location");
//        RestAssured.given().log().all().
//                when().
//                    delete(uri).
//                then().log().all().
//                    statusCode(HttpStatus.NO_CONTENT.value());
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
                then().log().all().
                    extract();

        RestAssured.
                given().log().all().
                    body(testLine2).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then().log().all();

        // when
        String uri = createResponse1.header("Location");
        RestAssured.
                given().log().all().
                    body(testLine3).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    put(uri).
                then().log().all().
                    statusCode(HttpStatus.OK.value()).log().all();
    }

    @DisplayName("존재하지 않는 id의 노선 이름을 변경한다.")
    @Test
    void change_name_no_id() {
        /// given
        RestAssured.
                given().log().all().
                    body(testLine1).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then();

        RestAssured.
                given().log().all().
                    body(testLine2).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then();

        // when
        RestAssured.
                given().log().all().
                    body(testLine3).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    put("/lines/{lineId}", 1000).
                then().
                    statusCode(HttpStatus.BAD_REQUEST.value());
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

        RestAssured.
                given().log().all().
                    body(testLine2).
                    contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                    post("/lines").
                then();

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
