package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dto.line.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Transactional
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLine() {
        // given
        Long upStationId = postStationAndGetId("강남역");
        Long downStationId = postStationAndGetId("선릉역");

        Map<String, String> param = new HashMap<>();
        param.put("name", "신분당선");
        param.put("color", "red");
        param.put("upStationId", String.valueOf(upStationId));
        param.put("downStationId", String.valueOf(downStationId));
        param.put("distance", "5");

        // when
        ExtractableResponse<Response> response = post("/lines", param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Long upStationId = postStationAndGetId("강남역");
        Long downStationId = postStationAndGetId("선릉역");

        Map<String, String> param = new HashMap<>();
        param.put("name", "신분당선");
        param.put("color", "red");
        param.put("upStationId", String.valueOf(upStationId));
        param.put("downStationId", String.valueOf(downStationId));
        param.put("distance", "5");

        post("/lines", param);

        // when
        ExtractableResponse<Response> response = post("/lines", param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 지하철역을 노선에 등록한다.")
    @Test
    void createLineWithBadStationId() {
        // given
        Map<String, String> param = new HashMap<>();
        param.put("name", "신분당선");
        param.put("color", "red");
        param.put("upStationId", "1");
        param.put("downStationId", "2");
        param.put("distance", "5");

        // when
        ExtractableResponse<Response> response = post("/lines", param);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("동일한 상행종점과 하행종점을 노선에 등록한다")
    @Test
    void createLineWithSameUpDown() {
        // given
        Long stationId = postStationAndGetId("선릉역");

        Map<String, String> param = new HashMap<>();
        param.put("name", "신분당선");
        param.put("color", "red");
        param.put("upStationId", String.valueOf(stationId));
        param.put("downStationId", String.valueOf(stationId));
        param.put("distance", "5");

        // when
        ExtractableResponse<Response> response = post("/lines", param);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("1미만의 거리를 노선에 등록한다")
    @Test
    void createLineWithWrongDistance() {
        // given
        Long upStationId = postStationAndGetId("강남역");
        Long downStationId = postStationAndGetId("선릉역");

        Map<String, String> param = new HashMap<>();
        param.put("name", "신분당선");
        param.put("color", "red");
        param.put("upStationId", String.valueOf(upStationId));
        param.put("downStationId", String.valueOf(downStationId));
        param.put("distance", "0");

        // when
        ExtractableResponse<Response> response = post("/lines", param);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("지하철 전체 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        Long firstLineId = postLineAndGetId("2호선",
                "green",
                postStationAndGetId("선릉역"),
                postStationAndGetId("구의역"),
                5);
        Long secondLineId = postLineAndGetId("5호선",
                "purple",
                postStationAndGetId("광화문역"),
                postStationAndGetId("광나루역"),
                10);

        // when
        ExtractableResponse<Response> response = get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = List.of(firstLineId, secondLineId);
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /// given
        Long firstLineId = postLineAndGetId("2호선",
                "green",
                postStationAndGetId("선릉역"),
                postStationAndGetId("구의역"),
                5);
        postLineAndGetId("5호선",
                "purple",
                postStationAndGetId("광화문역"),
                postStationAndGetId("광나루역"),
                10);

        // when
        ExtractableResponse<Response> response = get("/lines/" + firstLineId);
        Long resultId = response.jsonPath().getLong("id");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultId).isEqualTo(firstLineId);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLineWrongId() {
        // given

        // when
        ExtractableResponse<Response> response = get("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Long lineId = postLineAndGetId(
                "2호선",
                "green",
                postStationAndGetId("선릉역"),
                postStationAndGetId("구의역"),
                5);

        // when
        ExtractableResponse<Response> response = delete("/lines/" + lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @DisplayName("존재하지 않는 지하철 노선을 삭제한다.")
    @Test
    void deleteLineWrongId() {
        // given

        // when
        ExtractableResponse<Response> response = delete("/lines/1");

        // then
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Long lineId = postLineAndGetId("2호선",
                "green",
                postStationAndGetId("선릉역"),
                postStationAndGetId("강남역"),
                5);

        Map<String, String> param = Map.of(
                "name", "5호선",
                "color", "purple"
        );

        // when
        ExtractableResponse<Response> updateResponse = update("/lines/" + lineId, param);

        // then
        ExtractableResponse<Response> findResponse = get("/lines/" + lineId);
        String name = findResponse.jsonPath().getString("name");
        String color = findResponse.jsonPath().getString("color");

        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(name).isEqualTo("5호선");
        assertThat(color).isEqualTo("purple");
    }

    @DisplayName("기존에 존재하는 지하철 노선명으로 지하철 노선명을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given
        Long upStationId = postStationAndGetId("선릉역");
        Long downStationId = postStationAndGetId("강남역");
        Long lineId = postLineAndGetId("2호선", "green", upStationId, downStationId, 5);
        postLineAndGetId("5호선", "purple", upStationId, downStationId, 5);
        // when
        Map<String, String> existParam = Map.of(
                "name", "5호선",
                "color", "green"
        );
        ExtractableResponse<Response> updateResponse = update("/lines/" + lineId, existParam);

        // then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Long postStationAndGetId(String name) {
        return post("/stations", Map.of("name", name))
                .jsonPath()
                .getLong("id");
    }

    private Long postLineAndGetId(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> param = Map.of(
                "name", name,
                "color", color,
                "upStationId", String.valueOf(upStationId),
                "downStationId", String.valueOf(downStationId),
                "distance", String.valueOf(distance)
        );

        return post("/lines", param)
                .jsonPath()
                .getLong("id");
    }
}
