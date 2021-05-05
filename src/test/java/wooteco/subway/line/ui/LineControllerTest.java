package wooteco.subway.line.ui;

import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.repository.LineRepositoryImpl;
import wooteco.subway.line.ui.dto.LineRequest;

import javax.swing.*;

import java.net.URISyntaxException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineControllerTest {

    @Autowired
    private LineController lineController;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        LineRepositoryImpl.getInstance().clear();
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

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void allLines() throws URISyntaxException {
        lineController.createNewLine(new LineRequest("신분당선", "bg-red-600"));
        lineController.createNewLine(new LineRequest("2호선", "bg-green-600"));

        RestAssured
                .given().log().all()
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/lines")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body("id", contains(1,2));
    }

    @DisplayName("노선을 검색한다")
    @Test
    void findById_findLineById() throws URISyntaxException {
        lineController.createNewLine(new LineRequest("신분당선", "bg-red-600"));
        lineController.createNewLine(new LineRequest("2호선", "bg-green-600"));

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

}