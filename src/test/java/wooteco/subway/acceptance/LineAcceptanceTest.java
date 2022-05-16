package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);

        // when
        ExtractableResponse<Response> response = createLine(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);

        createLine(lineRequest);

        // when
        ExtractableResponse<Response> response = createLine(lineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /// given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("교대역");
        StationRequest stationRequest4 = new StationRequest("수서역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);
        Station station4 = createStation(stationRequest4).as(Station.class);

        LineRequest lineRequest1 = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        LineRequest lineRequest2 = new LineRequest("3호선", "orange", station3.getId(), station4.getId(), 10);

        ExtractableResponse<Response> createResponse1 = createLine(lineRequest1);
        ExtractableResponse<Response> createResponse2 = createLine(lineRequest2);

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

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);

        ExtractableResponse<Response> createResponse = createLine(lineRequest);

        // when
        long expectedLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + expectedLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("조회할 지하철 노선이 없는 경우 예외가 발생한다.")
    @Test
    void getNotExistLine() {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + 1)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);

        // when
        ExtractableResponse<Response> response = createLine(lineRequest);

        long savedLineId = Long.parseLong(response.header("Location").split("/")[2]);

        LineRequest updateRequest = new LineRequest("3호선", "orange");

        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + savedLineId)
                .then().log().all()
                .extract();

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);

        ExtractableResponse<Response> createResponse = createLine(lineRequest);

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
