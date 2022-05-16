package wooteco.subway.acceptance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static wooteco.subway.acceptance.AcceptanceFixture.*;

public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach()
    void setStation() {
        // 지하철 등록
        insert(new StationRequest("강남역"), "/stations", 201);
        insert(new StationRequest("역삼역"), "/stations", 201);
        insert(new StationRequest("선릉역"), "/stations", 201);
        insert(new StationRequest("잠실역"), "/stations", 201);
    }

    @DisplayName("지하철 노선 생성")
    @Test
    void createLines() {
        insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 201)
                .header("Location", is("/lines/1"))
                .body("name", is("신분당선"))
                .body("color", is("bg-red-600"));
    }

    @DisplayName("중복된 지하철 노선 생성 예외")
    @Test
    void checkDuplicateLine() {
        // given
        insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 201);

        // then
        insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 404);
    }

    @DisplayName("지하철 노선 조회 예외 - 존재하지 않는 노선 id")
    @Test
    void getLineException() {
        // given
        insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 201);

        // then
        select("/lines/" + 0L, 404);
    }

    @DisplayName("지하철 노선 조회")
    @Test
    void getLine() {
        // given
        long id = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 201)
                .extract().jsonPath().getLong("id");

        // then
        select("/lines/" + id, 200).body("stations.size()", is(2));
    }

    @DisplayName("지하철 노선 목록 조회")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 201).extract();
        ExtractableResponse<Response> newCreateResponse = insert(new LineRequest("분당선", "bg-green-600",
                1L, 2L, 10), "/lines", 201).extract();

        ExtractableResponse<Response> response = select("/lines", 200).extract();

        // then
        List<Long> expectedLineIds = Stream.of(createResponse, newCreateResponse)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선 수정")
    @Test
    void modifyLine() {
        // given
        long id = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 201)
                .extract().jsonPath().getLong("id");

        //then
        put("/lines/" + id, new LineRequest("분당선", "bg-red-600", 1L, 2L,
                        10), 200);
    }

    @DisplayName("지하철 노선 수정 예외 - 기존에 존재하던 노선 이름으로 변경한 경우")
    @Test
    void checkDuplicateSameName() {
        // given
        long id = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 201)
                .extract().jsonPath().getLong("id");
        insert(new LineRequest("분당선", "bg-green-600",
                2L, 3L, 10), "/lines", 201);

        //then
        put("/lines/" + id, new LineRequest("분당선", "bg-red-600", 1L, 2L, 10)
                , 404);
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() {
        // given
        long id = insert(new LineRequest("신분당선", "bg-red-600",
                1L, 2L, 10), "/lines", 201)
                .extract().jsonPath().getLong("id");

        //then
        delete("/lines/" + id, 204);
    }
}
