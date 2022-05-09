package wooteco.subway.acceptance;

import static io.restassured.RestAssured.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Line line;
    private ExtractableResponse<Response> response;
    private String lineName = "신분당선";
    private String lineColor = "bg-red-600";

    @BeforeEach
    void setBefore() {
        // given
        line = new Line(lineName, lineColor);
        response = RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        Long resultLineId = Long.parseLong(response.header("Location").split("/")[2]);
        get("/lines/" + resultLineId).then()
                .assertThat()
                .body("id", equalTo(resultLineId.intValue()))
                .body("name", equalTo(lineName))
                .body("color", equalTo(lineColor));
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        Line line2 = new Line("분당선", "bg-green-600");
        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(line2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> linesResponse = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(linesResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(response, response2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = linesResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 단일 노선을 조회한다.")
    @Test
    void getLineById() {
        // when
        Long resultLineId = Long.parseLong(response.header("Location").split("/")[2]);

        // then
        get("/lines/" + resultLineId).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(resultLineId.intValue()))
                .body("name", equalTo(lineName))
                .body("color", equalTo(lineColor));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // when
        Line line2 = new Line("다른분당선", "bg-red-600");
        Long resultLineId = Long.parseLong(response.header("Location").split("/")[2]);
        RestAssured.given().log().all()
                .body(line2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .extract();

        // then
        get("/lines/" + resultLineId).then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(resultLineId.intValue()))
                .body("name", equalTo("다른분당선"))
                .body("color", equalTo(lineColor));
    }

    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteStation() {
        // when
        String uri = response.header("Location");
        String resultLineId = uri.split("/")[2];
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        get("/lines/" + resultLineId).then()
                .assertThat()
                .body("message", equalTo("해당하는 노선이 존재하지 않습니다."));
    }
}
