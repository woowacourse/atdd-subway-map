package wooteco.subway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.service.LineService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private LineService lineService;

    private List<Long> testLineIds;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testLineIds = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        testLineIds.forEach(testLineId -> lineService.deleteLine(testLineId));
    }

    private ValidatableResponse postLineApi(LineRequest lineRequest) throws JsonProcessingException {
        ValidatableResponse validatableResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(OBJECT_MAPPER.writeValueAsString(lineRequest))
                .when().post("/lines")
                .then().log().all();
        addCreatedLineId(validatableResponse);
        return validatableResponse;
    }

    private void addCreatedLineId(ValidatableResponse validatableResponse) {
        long id = Long.parseLong(validatableResponse.extract()
                .header("Location")
                .split("/")[2]);
        testLineIds.add(id);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() throws Exception {
        LineRequest lineRequest = new LineRequest("2호선", "red", 1L, 3L, 7);
        LineResponse lineResponse = new LineResponse(1L, "2호선", "red", new ArrayList<>());

        postLineApi(lineRequest)
                .statusCode(HttpStatus.CREATED.value())
                .body(is(OBJECT_MAPPER.writeValueAsString(lineResponse)));
    }

    @DisplayName("전체 노선을 조회한다.")
    @Test
    void showLines() throws Exception {
        LineRequest lineRequest1 = new LineRequest("3호선", "red", 1L, 3L, 7);
        LineRequest lineRequest2 = new LineRequest("4호선", "blue", 2L, 3L, 4);
        postLineApi(lineRequest1);
        postLineApi(lineRequest2);

        List<Long> resultLineIds = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getList("", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds.containsAll(testLineIds)).isTrue();
    }

    @DisplayName("아이디로 노선을 조회한다.")
    @Test
    void showLine() throws JsonProcessingException {
        LineRequest lineRequest = new LineRequest("5호선", "red", 1L, 3L, 7);
        postLineApi(lineRequest);

        long id = testLineIds.get(0);
        LineResponse lineResponse = new LineResponse(id, "5호선", "red", new ArrayList<>());

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is(OBJECT_MAPPER.writeValueAsString(lineResponse)));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void editLine() throws JsonProcessingException {
        LineRequest lineRequest = new LineRequest("6호선", "red", 1L, 3L, 7);
        postLineApi(lineRequest);
        long id = testLineIds.get(0);
        LineRequest putRequest = new LineRequest("구분당선", "white", 1L, 3L, 7);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(OBJECT_MAPPER.writeValueAsString(putRequest))
                .when().put("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("없는 아이디의 지하철 노선은 수정할 수 없다.")
    @Test
    void cannotEditLineWhenNoId() throws JsonProcessingException {
        LineRequest putRequest = new LineRequest("구분당선", "white", 1L, 3L, 7);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(OBJECT_MAPPER.writeValueAsString(putRequest))
                .when().put("/lines/" + 9999)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 생성된 노선의 이름으로 수정할 수 없다.")
    @Test
    void cannotEditLineWhenDuplicateName() throws JsonProcessingException {
        LineRequest lineRequest1 = new LineRequest("6호선", "red", 1L, 3L, 7);
        LineRequest lineRequest2 = new LineRequest("신분당선", "red", 1L, 3L, 7);
        postLineApi(lineRequest1);
        postLineApi(lineRequest2);
        long id = testLineIds.get(0);
        LineRequest putRequest = new LineRequest("신분당선", "white", 1L, 3L, 7);

        RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(OBJECT_MAPPER.writeValueAsString(putRequest))
                .when().put("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("등록된 노선을 삭제한다.")
    @Test
    void deleteLine() throws JsonProcessingException {
        LineRequest lineRequest = new LineRequest("7호선", "green", 1L, 3L, 7);
        String uri = postLineApi(lineRequest).extract().header("Location");

        RestAssured.given().log().all()
                .when().delete(uri)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
