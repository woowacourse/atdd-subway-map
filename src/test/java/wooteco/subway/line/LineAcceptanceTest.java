package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.presentation.line.dto.LineRequest;
import wooteco.subway.presentation.line.dto.SectionRequest;
import wooteco.subway.presentation.station.dto.StationRequest;
import wooteco.subway.presentation.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparingInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@Sql(value = {"classpath:/line/lineQueryInit.sql", "classpath:/station/stationQueryInit.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("새로운 노선을 생성한다.")
    @Test
    void createNewline_createNewLineFromUserInputs() {
        insertStation("dummy1");
        insertStation("dummy2");

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10L))
                .post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Location", "/lines/1")
                .body("id", is(1))
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"))
                .extract();

        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        stations = new ArrayList<>(stations);
        stations.sort(comparingInt(station -> Math.toIntExact(station.getId())));

        assertThat(stations)
                .usingRecursiveComparison()
                .isEqualTo(
                        Arrays.asList(
                                new StationResponse(1L, "dummy1"),
                                new StationResponse(2L, "dummy2")
                        )
                );
    }

    @DisplayName("모든 노선을 조회한다.")
    void allLines() {
        insertLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        insertLine("신분당선", "bg-red-600", 1L, 2L, 10L);

        RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("id", contains(1, 2));
    }

    @DisplayName("노선을 검색한다")
    @Test
    void findById_findLineById() {
        insertStation("바보역");
        insertStation("보바역");
        insertLine("신분당선", "bg-red-600", 1L, 2L, 10L);
        insertLine("2호선", "bg-green-600", 1L, 2L, 10L);

        RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("id", is(1))
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"));
    }

    @DisplayName("노선이 없다면 400에러 발생")
    @Test
    void findById_canNotFindLineById() {
        RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노션을 수정한다.")
    @Test
    void modifyById_modifyLineFromUserInputs() {
        insertStation("바보역");
        insertStation("보바역");
        insertLine("신분당선", "bg-red-600", 1L, 2L, 10L);

        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new LineRequest("구분당선", "bg-red-600", 1L, 2L, 10L))
                .put("/lines/" + 1)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("노션을 삭제한다.")
    @Test
    void deleteById_deleteLineFromUserInputs() {
        insertLine("신분당선", "bg-red-600", 1L, 2L, 10L);

        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .when()
                .delete("/lines/1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("새로운 구간을 추가한다")
    @Test
    void createNewSection() {
        insertStation("바보역");
        insertStation("보바역");
        insertStation("난 바보가 아니다역");
        insertLine("바보선", "빨강:)", 1L, 2L, 10L);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL_VALUE)
                .when()
                .body(new SectionRequest(1L, 3L, 4L))
                .post("/lines/1/sections")
                .then().log().all()
                .body("id", is(1))
                .body("name", is("바보선"))
                .body("color", is("빨강:)"))
                .extract();

        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        assertThat(stations).
                usingRecursiveComparison()
                .isEqualTo(
                        Arrays.asList(
                                new StationResponse(1L, "바보역"),
                                new StationResponse(3L, "난 바보가 아니다역"),
                                new StationResponse(2L, "보바역")
                        )
                );
    }

    private void insertLine(String name, String color, Long upStationId, Long downStationId, Long distance) {
        RestAssured
                .given()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new LineRequest(name, color, upStationId, downStationId, distance))
                .post("/lines");
    }

    private void insertStation(String name) {
        RestAssured
                .given()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new StationRequest(name))
                .post("/stations");
    }
}