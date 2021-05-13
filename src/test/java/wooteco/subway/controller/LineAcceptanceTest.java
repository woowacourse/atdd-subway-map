package wooteco.subway.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
@Sql("/truncate.sql")
class LineAcceptanceTest extends AcceptanceTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private StationResponse upStation;
    private StationResponse downStation;
    private ExtractableResponse<Response> lineResponse;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        upStation = RestAssured.given()
            .when()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new StationRequest("잠실역"))
            .post("/stations")
            .then()
            .extract()
            .as(StationResponse.class);

        downStation = RestAssured.given()
            .when()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new StationRequest("잠실새내역"))
            .post("/stations")
            .then()
            .extract()
            .as(StationResponse.class);

        lineResponse = RestAssured.given()
            .when()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new LineRequest(
                "2호선", "green",
                upStation.getId(), downStation.getId(),
                5
            ))
            .post("/lines")
            .then()
            .extract();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() throws Exception {
        // given
        LineRequest lineRequest = new LineRequest(
            "3호선", "red",
            upStation.getId(), downStation.getId(),
            5
        );
        LineResponse response = new LineResponse(
            2L, "3호선", "red", Arrays.asList(upStation, downStation)
        );

        // when
        postLineApi(lineRequest)
            .statusCode(HttpStatus.valueOf(201).value())  // then
            .body(is(OBJECT_MAPPER.writeValueAsString(response)));
    }

    @DisplayName("전체 노선을 조회한다.")
    @Test
    void showLines() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ValidatableResponse postLineApi(LineRequest lineRequest)
        throws JsonProcessingException {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .body(OBJECT_MAPPER.writeValueAsString(lineRequest))
            .when()
            .post("/lines")
            .then().log().all();
    }

    @DisplayName("아이디로 노선을 조회한다.")
    @Test
    void showLine2() throws JsonProcessingException {
        long id = Long.parseLong(lineResponse.header("Location").split("/")[2]);
        LineResponse getResponse = new LineResponse(id, "2호선", "green", Arrays.asList(
            upStation,
            downStation
        ));

        // when
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/" + id)
            .then().log().all()
            .statusCode(HttpStatus.OK.value())  // then
            .body(is(OBJECT_MAPPER.writeValueAsString(getResponse)));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void editLine() throws JsonProcessingException {
        // given
        long id = Long.parseLong(lineResponse.header("Location").split("/")[2]);
        LineRequest putRequest = new LineRequest(
            "구분당선", "white",
            upStation.getId(), downStation.getId(),
            7
        );

        // when
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(OBJECT_MAPPER.writeValueAsString(putRequest))
            .when().put("/lines/" + id)
            .then().log().all()
            .statusCode(HttpStatus.OK.value());  // then
    }

    @DisplayName("없는 아이디의 지하철 노선은 수정할 수 없다.")
    @Test
    void cannotEditLineWhenNoId() throws JsonProcessingException {
        // given
        LineRequest putRequest = new LineRequest("구분당선", "white", 1L, 3L, 7);

        // when
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(OBJECT_MAPPER.writeValueAsString(putRequest))
            .when().put("/lines/" + Long.MAX_VALUE)
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());  // then
    }

    @DisplayName("이미 생성된 노선의 이름으로 수정할 수 없다.")
    @Test
    void cannotEditLineWhenDuplicateName() throws JsonProcessingException {
        // given
        LineRequest lineRequest = new LineRequest(
            "3호선", "red",
            upStation.getId(), downStation.getId(),
            7
        );
        ExtractableResponse<Response> response = postLineApi(lineRequest)
            .extract();
        postLineApi(lineRequest);

        long id = Long.parseLong(response.header("Location").split("/")[2]);
        LineRequest putRequest = new LineRequest(
            "2호선", "white",
            upStation.getId(), downStation.getId(),
            7
        );

        // when
        RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(OBJECT_MAPPER.writeValueAsString(putRequest))
            .when().put("/lines/" + id)
            .then().log().all()
            .statusCode(HttpStatus.BAD_REQUEST.value());  // then
    }

    @DisplayName("등록된 노선을 삭제한다.")
    @Test
    void deleteLine() throws JsonProcessingException {
        // given
        String uri = lineResponse.header("Location");

        // when
        RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());  // then
    }
}
