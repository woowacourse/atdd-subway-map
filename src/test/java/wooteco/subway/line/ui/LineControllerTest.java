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
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.line.ui.dto.LineCreateRequest;
import wooteco.subway.line.ui.dto.LineModifyRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static wooteco.subway.line.service.LineService.ERROR_DUPLICATED_LINE_NAME;

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
                .body(new LineRequest("신분당선", "bg-red-600"))
                .post("/lines")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Location", "/lines/1")
                .body("id", is(1))
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"));
    }

    @DisplayName("노선 이름 중복 체크")
    @Test
    void createNewLine_checkDuplicatedLineName() {
        lineRepository.save(new Line("신분당선", "bg-red-600"));

        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new LineRequest("신분당선", "bg-red-600"))
                .post("/lines")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is(ERROR_DUPLICATED_LINE_NAME));
    }


    @DisplayName("모든 노선을 조회한다.")
    @Test
    void allLines() {
        lineRepository.save(new Line("신분당선", "bg-red-600"));
        lineRepository.save(new Line("2호선", "bg-green-600"));


        lineRepository.save(new Line("신분당선", "bg-red-600", sections));
        lineRepository.save(new Line("2호선", "bg-green-600", sections));

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

        Sections sections = new Sections(
                Arrays.asList(
                        new Section(1L, 10L, 10),
                        new Section(1L, 20L, 10)));


        lineRepository.save(new Line("신분당선", "bg-red-600", sections));
        lineRepository.save(new Line("2호선", "bg-green-600", sections));

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

        Sections sections = new Sections(
                Arrays.asList(
                        new Section(1L, 10L, 10),
                        new Section(1L, 20L, 10)));


        final Line line = lineRepository.save(new Line("신분당선", "bg-red-600", sections));

        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(new LineRequest("구분당선", "bg-red-600"))
                .put("/lines/" + id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        final Line updatedLine = lineRepository.findById(line.getId());
        assertThat(updatedLine.getName()).isEqualTo("구분당선");


    }

    @DisplayName("노션을 삭제한다.")
    @Test
    void deleteById_deleteLineFromUserInputs() {
        Sections sections = new Sections(
                Arrays.asList(
                        new Section(1L, 10L, 10),
                        new Section(1L, 20L, 10)));

        lineRepository.save(new Line("신분당선", "bg-red-600", sections));

        RestAssured
                .given().log().all()
                .accept(MediaType.ALL_VALUE)
                .when()
                .delete("/lines/1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

}