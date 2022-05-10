package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.Fixture.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.response.LineResponseDto;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", stationIds.get(0).toString());
        params.put("downStationId", stationIds.get(1).toString());
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = createLineRequest(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", stationIds.get(0).toString());
        params.put("downStationId", stationIds.get(1).toString());
        params.put("distance", "10");
        createLineRequest(params);

        // when
        ExtractableResponse<Response> response = createLineRequest(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void showLines() {
        /// given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        Map<String, String> lineParams1 = new HashMap<>();
        lineParams1.put("name", "2호선");
        lineParams1.put("color", "bg-green-600");
        lineParams1.put("upStationId", stationIds.get(0).toString());
        lineParams1.put("downStationId", stationIds.get(1).toString());
        lineParams1.put("distance", "10");
        ExtractableResponse<Response> createResponse1 = createLineRequest(lineParams1);

        Map<String, String> lineParams2 = new HashMap<>();
        lineParams2.put("name", "신분당선");
        lineParams2.put("color", "bg-red-600");
        lineParams2.put("upStationId", stationIds.get(0).toString());
        lineParams2.put("downStationId", stationIds.get(1).toString());
        lineParams2.put("distance", "10");
        ExtractableResponse<Response> createResponse2 = createLineRequest(lineParams2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponseDto.class).stream()
                .map(LineResponseDto::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("id 로 노선을 조회한다.")
    @Test
    void showLine() {
        /// given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");

        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", stationIds.get(0).toString());
        params.put("downStationId", stationIds.get(1).toString());
        params.put("distance", "10");
        ExtractableResponse<Response> createResponse = createLineRequest(params);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
        LineResponseDto responseBody = response.jsonPath().getObject(".", LineResponseDto.class);

        // then
        assertAll(
                () -> assertThat(responseBody.getName()).isEqualTo("2호선"),
                () -> assertThat(responseBody.getColor()).isEqualTo("bg-green-600")
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", stationIds.get(0).toString());
        params.put("downStationId", stationIds.get(1).toString());
        params.put("distance", "10");
        ExtractableResponse<Response> createResponse = createLineRequest(params);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        Map<String, String> modifyParams = new HashMap<>();
        modifyParams.put("name", "신분당선");
        modifyParams.put("color", "bg-red-600");
        ExtractableResponse<Response> modifyResponse = RestAssured.given().log().all()
                .body(modifyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all()
                .extract();

        // then
        assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void removeLine() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", stationIds.get(0).toString());
        params.put("downStationId", stationIds.get(1).toString());
        params.put("distance", "10");
        ExtractableResponse<Response> createResponse = createLineRequest(params);

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

    @Test
    @DisplayName("노선에 새로운 구간을 추가한다.")
    void addSection() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        Map<String, String> lineParams = new HashMap<>();
        lineParams.put("name", "2호선");
        lineParams.put("color", "bg-green-600");
        lineParams.put("upStationId", stationIds.get(0).toString());
        lineParams.put("downStationId", stationIds.get(1).toString());
        lineParams.put("distance", "10");
        ExtractableResponse<Response> lineCreateResponse = createLineRequest(lineParams);
        final LineResponseDto createdLine = lineCreateResponse.jsonPath()
                .getObject(".", LineResponseDto.class);

        final List<Long> newStationIds = save2StationsRequest("삼성역", "봉은사역");

        Map<String, String> sectionParams = new HashMap<>();
        sectionParams.put("upStationId", stationIds.get(0).toString());
        sectionParams.put("downStationId", newStationIds.get(0).toString());
        sectionParams.put("distance", "5");

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .body(sectionParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines/" + createdLine.getId() + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("노선에서 역을 삭제한다.")
    void removeStation() {
        // given
        final List<Long> stationIds = save2StationsRequest("선릉역", "잠실역");
        Map<String, String> lineParams = new HashMap<>();
        lineParams.put("name", "2호선");
        lineParams.put("color", "bg-green-600");
        lineParams.put("upStationId", stationIds.get(0).toString());
        lineParams.put("downStationId", stationIds.get(1).toString());
        lineParams.put("distance", "10");
        ExtractableResponse<Response> lineCreateResponse = createLineRequest(lineParams);
        final LineResponseDto createdLine = lineCreateResponse.jsonPath()
                .getObject(".", LineResponseDto.class);

        final Long newStationId = save2StationsRequest("삼성역", "봉은사역").get(0);

        Map<String, String> sectionParams = new HashMap<>();
        sectionParams.put("upStationId", stationIds.get(0).toString());
        sectionParams.put("downStationId", newStationId.toString());
        sectionParams.put("distance", "5");

        RestAssured.given().log().all()
                .when()
                .body(sectionParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines/" + createdLine.getId() + "/sections")
                .then().log().all()
                .extract();

        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .body(sectionParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .delete("/lines/" + createdLine.getId() + "/sections?stationId=" + newStationId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
