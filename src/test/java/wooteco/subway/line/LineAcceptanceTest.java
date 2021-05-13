package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.station.controller.dto.StationRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@DisplayName("지하철 노선 관련 기능")
@Transactional
public class LineAcceptanceTest extends AcceptanceTest {

    private final LineRequest line2Request;
    private final LineRequest line3Request;
    private final StationRequest gangnamStationRequest;
    private final StationRequest yeoksamStationRequest;

    public LineAcceptanceTest() {
        this.line2Request = new LineRequest("2호선", "bg-red-600", 1L, 2L, 4);
        this.line3Request = new LineRequest("3호선", "bg-red-600", 1L, 2L, 5);
        this.gangnamStationRequest = new StationRequest("강남역");
        this.yeoksamStationRequest = new StationRequest("역삼역");
    }

    @DisplayName("지하철 노선 생성한다.")
    @Test
    void createLine() {
        // given
        createStation(gangnamStationRequest);
        createStation(yeoksamStationRequest);

        //when
        ExtractableResponse<Response> response = createLine(line2Request);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("지하철 노선 목록을 보여준다.")
    void showLines() {
        // given
        ExtractableResponse<Response> createResponse1 = createStation(gangnamStationRequest);
        ExtractableResponse<Response> createResponse2 = createStation(yeoksamStationRequest);
        ExtractableResponse<Response> createResponse3 = createLine(line2Request);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 1개를 보여준다.")
    @Test
    void showLine() {
        // given
        createStation(gangnamStationRequest);
        createStation(yeoksamStationRequest);
        ExtractableResponse<Response> createResponse = createLine(line2Request);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {

        // given
        createStation(gangnamStationRequest);
        createStation(yeoksamStationRequest);
        createLine(line2Request);
        ExtractableResponse<Response> createResponse = createLine(line3Request);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .body("size()", is(1));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        createStation(gangnamStationRequest);
        createStation(yeoksamStationRequest);
        ExtractableResponse<Response> createResponse = createLine(line2Request);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(line3Request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createStation(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
