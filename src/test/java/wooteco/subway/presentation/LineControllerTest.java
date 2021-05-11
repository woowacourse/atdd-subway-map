package wooteco.subway.presentation;

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
<<<<<<< HEAD:src/test/java/wooteco/subway/ui/LineControllerTest.java
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD:src/test/java/wooteco/subway/line/ui/LineControllerTest.java
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.domain.section.Sections;
import wooteco.subway.line.domain.value.LineColor;
import wooteco.subway.line.domain.value.LineName;
import wooteco.subway.line.ui.dto.LineRequest;
=======
import wooteco.subway.domain.line.Line;
=======
>>>>>>> d2a85ea... refactor: 테스트 및 버그 수정
=======
import org.springframework.web.bind.annotation.PostMapping;
>>>>>>> 110acd7... feat: 섹션 삭제 기능 추가
=======
>>>>>>> 0d2741d... refactor: 페키지 구조 변경:src/test/java/wooteco/subway/presentation/LineControllerTest.java
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.domain.station.StationRepository;
import wooteco.subway.presentation.line.dto.SectionRequest;
import wooteco.subway.presentation.line.dto.LineRequest;
import wooteco.subway.presentation.station.dto.StationRequest;
import wooteco.subway.presentation.station.dto.StationResponse;
import wooteco.util.StationFactory;
>>>>>>> e735a30... refactor: 지하철 노선 추가 API 수정 및 페키지 구조 변경:src/test/java/wooteco/subway/ui/LineControllerTest.java

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparingInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@Sql("classpath:line/lineQueryInit.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineControllerTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        jdbcTemplate.update("ALTER TABLE STATION ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM STATION");
        jdbcTemplate.update("ALTER TABLE LINE ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM LINE");
        jdbcTemplate.update("ALTER TABLE SECTION ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM SECTION");
    }

    @DisplayName("새로운 노선을 생성한다.")
    @Test
    void createNewline_createNewLineFromUserInputs() {
        stationRepository.save(StationFactory.create("dummy1"));
        stationRepository.save(StationFactory.create("dummy2"));

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

        assertThat(stations.get(0).getId()).isEqualTo(1L);
        assertThat(stations.get(0).getName()).isEqualTo("dummy1");
        assertThat(stations.get(1).getId()).isEqualTo(2L);
        assertThat(stations.get(1).getName()).isEqualTo("dummy2");
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