package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.common.ErrorResponse;
import wooteco.subway.station.StationDao;
import wooteco.subway.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관리 기능")
class LineAcceptanceTest extends AcceptanceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.execute("SET foreign_key_checks=0;");
        jdbcTemplate.execute("truncate table LINE");
        jdbcTemplate.execute("alter table LINE alter column ID restart with 1");
        jdbcTemplate.execute("SET foreign_key_checks=1;");
        jdbcTemplate.execute("truncate table STATION");
        jdbcTemplate.execute("alter table STATION alter column ID restart with 1");
        jdbcTemplate.execute("truncate table SECTION");
        jdbcTemplate.execute("alter table SECTION alter column ID restart with 1");
        stationDao.save("가양역");
        stationDao.save("증미역");
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        ExtractableResponse<Response> response = createLineInsertResponse("초록색", "2호선");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        Integer queryResult = jdbcTemplate.queryForObject(
                "select count(*) from SECTION where line_id = 1 and up_station_id = 1", Integer.class);
        assertThat(queryResult).isEqualTo(1);
    }

    @Test
    @DisplayName("유효성 검사에 걸리는 이름과 색상의 지하철 노선을 생선한다.")
    void createInValidLine() {
        ExtractableResponse<Response> response = createLineInsertResponse(" ", " ");
        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getDetail()).contains("Validation failed for argument [0] in public org.springframework.http.ResponseEntity<wooteco.subway.line.LineResponse> wooteco.subway.line.LineController.createLine(wooteco.subway.line.LineRequest) with 2 errors:");
        assertThat(errorResponse.getMessage()).isEqualTo("VALIDATION_FAILED");
    }

    @Test
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철노선을 생성한다.")
    void createLineWithDuplicateName() {
        // given & when
        createLineInsertResponse("초록색", "2호선");
        ExtractableResponse<Response> response = createLineInsertResponse("초록색", "2호선");
        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getDetail()).contains("존재하는 노선 이름입니다.");
        assertThat(errorResponse.getMessage()).contains("LINE_EXCEPTION");
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineInsertResponse("초록색", "2호선");
        ExtractableResponse<Response> createResponse2 = createLineInsertResponse("파란색", "1호선");

        // when
        ExtractableResponse<Response> response = RestAssured.given()
                .when()
                .get("/lines")
                .then()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location")
                        .split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("지하철 노선 1개를 조회한다.")
    void getLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse("초록색", "2호선");
        String uri = extract.header("Location");

        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .extract();

        LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse)
                .usingRecursiveComparison()
                .ignoringFields("stations")
                .isEqualTo(new LineResponse(1L, "2호선", "초록색"));

        assertThat(lineResponse.getStations())
                .containsExactly(
                        new StationResponse(1L, "가양역"),
                        new StationResponse(2L, "증미역")
                );
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선 1개를 조회한다.")
    void findNotExistingLineByName() {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then()
                .extract();
        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("LINE_EXCEPTION");
        assertThat(errorResponse.getDetail()).isEqualTo("노선을 찾지 못했습니다.");
    }

    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void modifyLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse("초록색", "2호선");
        String uri = extract.header("Location");
        LineRequest lineRequest = new LineRequest("9호선", "남색", 2L, 3L, 10);
        ExtractableResponse<Response> response = RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("유효하지 않은 값으로 지하철 노선을 수정한다.")
    public void modifyWithInValidLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse("초록색", "2호선");
        String uri = extract.header("Location");

        LineRequest lineRequest = new LineRequest(" ", " ", 2L, 3L, 10);
        ExtractableResponse<Response> response = RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then()
                .extract();

        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getDetail()).contains("Validation failed for argument [1] in public org.springframework.http.ResponseEntity<java.lang.Void> wooteco.subway.line.LineController.modifyLine(java.lang.Long,wooteco.subway.line.LineRequest) with 2 errors:");
        assertThat(errorResponse.getMessage()).isEqualTo("VALIDATION_FAILED");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 수정한다.")
    public void modifyWithNotExistingLine() {
        LineRequest lineRequest = new LineRequest("존재안함", "아무색", 2L, 3L, 10);
        ExtractableResponse<Response> response = RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/100")
                .then()
                .extract();

        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("LINE_EXCEPTION", "노선을 찾지 못했습니다."));
    }

    @Test
    @DisplayName("지하철 노선을 삭제한다.")
    void deleteLine() {
        ExtractableResponse<Response> extract = createLineInsertResponse("2호선", "초록색");

        String uri = extract.header("Location");
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("존재하지 않는 지하철 노선을 삭제한다.")
    public void deleteNotExistingLine() {
        //given & when
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/1")
                .then()
                .extract();
        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("LINE_EXCEPTION");
        assertThat(errorResponse.getDetail()).isEqualTo("노선을 찾지 못했습니다.");
    }

    private ExtractableResponse<Response> createLineInsertResponse(String color, String name) {
        LineRequest lineRequest = new LineRequest(name, color, 1L, 2L, 10);

        return RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
    }
}