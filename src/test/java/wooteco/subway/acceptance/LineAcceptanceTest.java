package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    private Long createdStationId1;
    private Long createdStationId2;

    @BeforeEach
    void createStations() {
        createdStationId1 = AcceptanceUtil.createStation("선릉역");
        createdStationId2 = AcceptanceUtil.createStation("잠실역");
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        String name = "2호선";
        String color = "bg-green-600";
        Long upStationId = createdStationId1;
        Long downStationId = createdStationId2;
        Integer distance = 10;

        // when
        ExtractableResponse<Response> response = requestCreateLine(name, color, upStationId, downStationId, distance);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        String name1 = "1호선";
        String color1 = "bg-green-600";

        String name2 = "2호선";
        String color2 = "bg-red-600";

        Long upStationId = createdStationId1;
        Long downStationId = createdStationId2;
        Integer distance = 10;

        ExtractableResponse<Response> createResponse1 = requestCreateLine(name1, color1, upStationId, downStationId,
                distance);
        ExtractableResponse<Response> createResponse2 = requestCreateLine(name2, color2, upStationId, downStationId,
                distance);

        // when
        ExtractableResponse<Response> response = requestGetAllLines();
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(actualLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("개별 노선을 ID 값으로 조회한다.")
    @Test
    void getLineById() {
        // given
        String name = "1호선";
        String color = "bg-green-600";
        Long upStationId = createdStationId1;
        Long downStationId = createdStationId2;
        Integer distance = 10;

        ExtractableResponse<Response> createResponse = requestCreateLine(name, color, upStationId, downStationId,
                distance);

        Long createId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = requestGetLineById(createId);

        // then
        Long responseId = response.jsonPath().getLong("id");
        String responseName = response.jsonPath().getString("name");
        String responseColor = response.jsonPath().getString("color");

        assertAll(
                () -> assertThat(responseId).isEqualTo(createId),
                () -> assertThat(responseName).isEqualTo(name),
                () -> assertThat(responseColor).isEqualTo(color)
        );
    }

    @DisplayName("노선 정보를 수정한다.")
    @Test
    void updateLineById() {
        // given
        String name = "1호선";
        String color = "bg-green-600";
        Long upStationId = createdStationId1;
        Long downStationId = createdStationId2;
        Integer distance = 10;

        ExtractableResponse<Response> createResponse = requestCreateLine(name, color, upStationId, downStationId,
                distance);
        Long createdId = getIdFromResponse(createResponse);

        // when
        String newName = "2호선";
        String newColor = "bg-green-600";
        requestUpdateLine(createdId, newName, newColor);
        ExtractableResponse<Response> response = requestGetLineById(createdId);

        // then
        String responseName = response.jsonPath().getString("name");
        String responseColor = response.jsonPath().getString("color");

        assertAll(
                () -> assertThat(responseName).isEqualTo(newName),
                () -> assertThat(responseColor).isEqualTo(newColor)
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        String name = "1호선";
        String color = "bg-green-600";
        Long upStationId = createdStationId1;
        Long downStationId = createdStationId2;
        Integer distance = 10;
        ExtractableResponse<Response> createResponse = requestCreateLine(name, color, upStationId, downStationId,
                distance);

        Long createdId = getIdFromResponse(createResponse);

        // when
        ExtractableResponse<Response> response = requestDeleteLine(createdId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철역을 생성할 경우 BAD REQUEST가 반환된다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        String name = "1호선";
        String color = "bg-green-600";
        Long upStationId = createdStationId1;
        Long downStationId = createdStationId2;
        Integer distance = 10;

        requestCreateLine(name, color, upStationId, downStationId, distance);

        // when
        ExtractableResponse<Response> response = requestCreateLine(name, color, upStationId, downStationId, distance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않은 지하철 노선을 조회하려 할 경우 NOT FOUND가 반환된다.")
    @Test
    void getLine_returnsBadRequestWithNotExistingId() {
        // given & when
        ExtractableResponse<Response> response = requestGetLineById(3L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("존재하지 않은 지하철 노선을 수정하려 할 경우 NOT FOUND가 반환된다.")
    @Test
    void updateLine_returnsBadRequestWithNotExistingId() {
        // given & when
        ExtractableResponse<Response> response = requestUpdateLine(3L, "신분당선", "bg-red-600");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("존재하지 않은 지하철 노선을 제거하려 할 경우 NOT FOUND가 반환된다.")
    @Test
    void deleteLine_returnsBadRequestWithNotExistingId() {
        // given & when
        ExtractableResponse<Response> response = requestDeleteLine(3L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> requestCreateLine(String name, String color, Long upStationId,
                                                            Long downStationId,
                                                            Integer distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance.toString());

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGetLineById(Long createdId) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + createdId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGetAllLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestUpdateLine(Long createdId, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + createdId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestDeleteLine(Long createdId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + createdId)
                .then().log().all()
                .extract();
    }

    private Long getIdFromResponse(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("lines/")[1]);
    }
}
