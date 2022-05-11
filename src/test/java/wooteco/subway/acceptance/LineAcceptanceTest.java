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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.dto.response.LineResponseDto;

@DisplayName("노선 관련 기능")
@Sql("/lineAcceptanceTest.sql")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response =
                getCreateLineResponse("2호선", "bg-green-600", 1L, 2L, 10);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    private ExtractableResponse<Response> getCreateLineResponse(String name,
                                                                String color,
                                                                Long upStationId,
                                                                Long downStationId,
                                                                int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        getCreateLineResponse("2호선", "bg-green-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response =
                getCreateLineResponse("2호선", "bg-yellow-600", 2L, 3L, 30);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void showLines() {
        /// given
        ExtractableResponse<Response> createResponse1 =
                getCreateLineResponse("2호선", "bg-green-600", 1L, 2L, 10);
        ExtractableResponse<Response> createResponse2 =
                getCreateLineResponse("3호선", "bg-yellow-600", 2L, 3L, 30);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponseDto.class).stream()
                .map(LineResponseDto::getId)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @DisplayName("id 로 노선을 조회한다.")
    @Test
    void showLine() {
        /// given
        ExtractableResponse<Response> createResponse =
                getCreateLineResponse("2호선", "bg-green-600", 1L, 2L, 10);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();

        // then
        LineResponseDto responseBody = response.jsonPath().getObject(".", LineResponseDto.class);
        assertAll(
                () -> assertThat(responseBody.getName()).isEqualTo("2호선"),
                () -> assertThat(responseBody.getColor()).isEqualTo("bg-green-600"),
                () -> assertThat(responseBody.getStations().size()).isEqualTo(2),
                () -> assertThat(responseBody.getStations().get(0).getName()).isEqualTo("강남역"),
                () -> assertThat(responseBody.getStations().get(1).getName()).isEqualTo("역삼역")
        );
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        ExtractableResponse<Response> createResponse =
                getCreateLineResponse("2호선", "bg-green-600", 1L, 2L, 10);
        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        Map<String, String> modifyParams = new HashMap<>();
        modifyParams.put("name", "신분당선");
        modifyParams.put("color", "bg-red-600");
        ExtractableResponse<Response> modifyResponse = RestAssured.given().log().all()
                .body(modifyParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", id)
                .then().log().all()
                .extract();
        LineResponseDto responseDto = RestAssured.given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract().jsonPath().getObject(".", LineResponseDto.class);

        // then
        assertAll(
                () -> assertThat(modifyResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(responseDto.getName()).isEqualTo("신분당선"),
                () -> assertThat(responseDto.getColor()).isEqualTo("bg-red-600")
        );
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void removeLine() {
        // given
        ExtractableResponse<Response> createResponse =
                getCreateLineResponse("강남역", "bg-red-600", 1L, 2L, 10);

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

    @DisplayName("구간을 등록한다.")
    @Test
    void createSection() {
        // given
        getCreateLineResponse("2호선", "bg-green-600", 1L, 3L, 100);

        // when
        ExtractableResponse<Response> response = getCreateSectionResponse(1L, 2L, 3L, 30);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> getCreateSectionResponse(Long lineId,
                                                                   Long upStationId,
                                                                   Long downStationId,
                                                                   int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void removeSection() {
        // given
        getCreateLineResponse("2호선", "bg-green-600", 1L, 3L, 100);
        getCreateSectionResponse(1L, 2L, 3L, 30);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/1/sections?stationId=2")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
