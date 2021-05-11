package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
@Sql("/truncate.sql")
class LineAcceptanceTest extends AcceptanceTest {

    private ExtractableResponse<Response> response;
    private Long upId;
    private Long downId;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        upId = RestAssured.given().log().all()
            .body(new StationRequest("강남역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract().as(StationResponse.class)
            .getId();

        downId = RestAssured.given().log().all()
            .body(new StationRequest("역삼역"))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract().as(StationResponse.class)
            .getId();

        response = RestAssured.given().log().all()
            .body(new LineRequest("분당선", "bg-yellow-600", upId, downId, 1))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        LineResponse lineResponse = response.body().as(LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(lineResponse.getName()).isEqualTo("분당선");
        assertThat(lineResponse.getColor()).isEqualTo("bg-yellow-600");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).hasSize(1);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        ExtractableResponse<Response> actualResponse = RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", response.header("Location").split("/")[2])
            .then().log().all()
            .extract();
        LineResponse lineResponse = actualResponse.body().as(LineResponse.class);

        assertThat(actualResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo("분당선");
        assertThat(lineResponse.getColor())
            .isEqualTo("bg-yellow-600");
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        ExtractableResponse<Response> putResponse = RestAssured.given().log().all()
            .body(new LineRequest("6호선", "bg-blue-600", null, null, 1))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}", response.header("Location").split("/")[2])
            .then().log().all()
            .extract();
        ExtractableResponse<Response> actualResponse = RestAssured.given().log().all()
            .when()
            .get("/lines/{id}", response.header("Location").split("/")[2])
            .then().log().all()
            .extract();
        LineResponse lineResponse = actualResponse.body().as(LineResponse.class);

        assertThat(putResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo("6호선");
        assertThat(lineResponse.getColor())
            .isEqualTo("bg-blue-600");
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> actualResponse = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete("/lines/{id}", response.header("Location").split("/")[2])
            .then().log().all()
            .extract();

        assertThat(actualResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
