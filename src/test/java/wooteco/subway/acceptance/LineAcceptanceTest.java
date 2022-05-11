package wooteco.subway.acceptance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptance.AcceptanceFixture.*;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLines() {
        // given
        insert(new StationRequest("강남역"), "/stations");
        insert(new StationRequest("역삼역"), "/stations");
        ExtractableResponse<Response> response = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines");

        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-red-600");
        assertThat(response.header("Location")).isEqualTo("/lines/1");
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        insert(new StationRequest("강남역"), "/stations");
        insert(new StationRequest("역삼역"), "/stations");

        ExtractableResponse<Response> createResponse = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines");
        ExtractableResponse<Response> newCreateResponse = insert(new LineRequest("분당선", "bg-green-600",
                1L, 2L, 10), "/lines");

        ExtractableResponse<Response> response = select("/lines");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse, newCreateResponse)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        insert(new StationRequest("강남역"), "/stations");
        insert(new StationRequest("역삼역"), "/stations");

        ExtractableResponse<Response> response = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines");
        long resultLineId = response.jsonPath().getLong("id");

        // then
        ExtractableResponse<Response> newResponse = select("/lines/" + resultLineId);
        assertThat(resultLineId).isEqualTo(newResponse.jsonPath().getLong("id"));
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void modifyLine() {
        // given
        insert(new StationRequest("강남역"), "/stations");
        insert(new StationRequest("역삼역"), "/stations");

        ExtractableResponse<Response> response = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines");
        long resultLineId = response.jsonPath().getLong("id");

        //then
        RestAssured.given().log()
                .all()
                .body(new LineRequest("분당선", "bg-red-600", 1L, 2L, 10))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + resultLineId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        insert(new StationRequest("강남역"), "/stations");
        insert(new StationRequest("역삼역"), "/stations");
        ExtractableResponse<Response> response = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines");
        long resultLineId = response.jsonPath().getLong("id");

        //then
        delete("/lines/" + resultLineId);
    }
}
