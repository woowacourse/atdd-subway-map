package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ObjectMapper om;

    @BeforeEach
    void beforeEach() throws JsonProcessingException {
        createStation1();
        createStation2();
    }

    private void createStation1() throws JsonProcessingException {
        StationRequest stationRequest1 = new StationRequest("강남역");
        String params1 = om.writeValueAsString(stationRequest1);
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        StationRequest stationRequest2 = new StationRequest("역삼역");
        String params2 = om.writeValueAsString(stationRequest2);
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private void createStation2() throws JsonProcessingException {
        StationRequest stationRequest1 = new StationRequest("모란역");
        String params1 = om.writeValueAsString(stationRequest1);
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        StationRequest stationRequest2 = new StationRequest("정자역");
        String params2 = om.writeValueAsString(stationRequest2);
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() throws JsonProcessingException {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        String params = om.writeValueAsString(lineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Disabled
    void createLineWithDuplicateName() throws JsonProcessingException {
        // given
        Line line1 = new Line("신분당선", "bg-red-600");
        String params1 = om.writeValueAsString(line1);

        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        Line line2 = new Line("신분당선", "bg-red-400");
        String params2 = om.writeValueAsString(line2);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("기존에 존재하는 노선 색으로 노선을 생성한다.")
    @Disabled
    void createLineWithDuplicateColor() throws JsonProcessingException {
        // given
        Line line = new Line("신분당선", "bg-red-600");
        String params1 = om.writeValueAsString(line);

        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        Line line2 = new Line("분당선", "bg-red-600");
        String params2 = om.writeValueAsString(line2);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선을 조회한다.")
    @Disabled
    void getLines() throws JsonProcessingException {
        /// given
        Line line1 = new Line("신분당선", "bg-red-600");
        String params1 = om.writeValueAsString(line1);
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        Line line2 = new Line("2호선", "bg-green-600");
        String params2 = om.writeValueAsString(line2);
        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
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

    @Test
    @DisplayName("노선을 하나 조회한다.")
    @Disabled
    void getLine() throws JsonProcessingException {
        /// given
        Line line = new Line("신분당선", "bg-red-600");
        String params = om.writeValueAsString(line);

        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        long expectId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + expectId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 수정한다.")
    @Disabled
    void updateLine() throws JsonProcessingException {
        /// given
        Line line1 = new Line("신분당선", "bg-red-600");
        String params1 = om.writeValueAsString(line1);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        Line line2 = new Line("다른분당선", "bg-blue-600");
        String params2 = om.writeValueAsString(line2);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선을 제거한다.")
    @Disabled
    void deleteLine() throws JsonProcessingException {
        // given
        Line line = new Line("신분당선", "bg-red-600");
        String params = om.writeValueAsString(line);
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params)
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
}
