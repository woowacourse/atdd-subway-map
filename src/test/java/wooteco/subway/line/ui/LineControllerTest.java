package wooteco.subway.line.ui;

import io.restassured.RestAssured;
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
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.domain.section.Sections;
import wooteco.subway.line.domain.value.LineColor;
import wooteco.subway.line.domain.value.LineName;
import wooteco.subway.line.ui.dto.LineRequest;

import java.net.URISyntaxException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@Sql("classpath:line/lineQueryInit.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineControllerTest {

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        jdbcTemplate.update("ALTER TABLE LINE ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("DELETE FROM LINE");
    }

    @DisplayName("새로운 노선을 생성한다.")
    @Test
    void createNewline_createNewLineFromUserInputs() {
        RestAssured
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
                    .body("color", is("bg-red-600"));
/*                    .body("upStationId", is(1))
                    .body("downStationId", is(1))
                    .body("distance", is(10));*/
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void allLines() throws URISyntaxException {
        lineRepository.save(new Line(
                new LineName("신분당선"),
                new LineColor("bg-red-600"),
                new Sections(Collections.emptyList()))
        );
        lineRepository.save(new Line(
                new LineName("2호선"),
                new LineColor("bg-green-600"),
                new Sections(Collections.emptyList()))
        );

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
        lineRepository.save(new Line(
                new LineName("신분당선"),
                new LineColor("bg-red-600"),
                new Sections(Collections.emptyList()))
        );

        lineRepository.save(new Line(
                new LineName("2호선"),
                new LineColor("bg-green-600"),
                new Sections(Collections.emptyList()))
        );

        RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then()
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
        lineRepository.save(new Line(
                new LineName("신분당선"),
                new LineColor("bg-red-600"),
                new Sections(Collections.emptyList())
        ));

        RestAssured
                .given().log().all()
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .body(new LineRequest("구분당선", "bg-red-600", 1L, 2L, 10L))
                    .put("/lines/" + 1)
                .then()
                    .statusCode(HttpStatus.OK.value());

        final Line line = lineRepository.findById(1L);

        assertThat(line.getLineName()).isEqualTo("구분당선");
    }

    @DisplayName("노션을 삭제한다.")
    @Test
    void deleteById_deleteLineFromUserInputs() {
        lineRepository.save(new Line(
                new LineName("신분당선"),
                new LineColor("bg-red-600"),
                new Sections(Collections.emptyList()))
        );

        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .when()
                .delete("/lines/1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

}