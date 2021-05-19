package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public
class LineAcceptanceTest extends AcceptanceTest {

    private LineResponse response;
    private Long lineId;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        Long upId = 강남역_response.as(StationResponse.class).getId();
        Long downId = 역삼역_response.as(StationResponse.class).getId();

        LineRequest lineRequest = new LineRequest("분당선", "bg-yellow-600", upId, downId, 1);
        response = postLine(lineRequest).as(LineResponse.class);
        lineId = response.getId();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        assertThat(response.getName()).isEqualTo("분당선");
        assertThat(response.getColor()).isEqualTo("bg-yellow-600");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        List<Long> resultLineIds = getLines().jsonPath().getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).hasSize(1);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() {
        ExtractableResponse<Response> actualResponse = getLine(lineId);
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
            .put("/lines/{id}", lineId)
            .then().log().all()
            .extract();
        ExtractableResponse<Response> actualResponse = getLine(lineId);
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
            .delete("/lines/{id}", lineId)
            .then().log().all()
            .extract();

        assertThat(actualResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
