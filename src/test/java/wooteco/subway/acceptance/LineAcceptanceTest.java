package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void createStations(){
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        createStationResponseOf(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        createStationResponseOf(params2);
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "red");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);

        // when
        ExtractableResponse<Response> response = createLineResponseOf(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.header("Location").split("/")[2]).isEqualTo("1");
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "red");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);

        createLineResponseOf(params);

        // when
        ExtractableResponse<Response> response = createLineResponseOf(params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, Object> params1 = new HashMap<>();
        params1.put("name", "1호선");
        params1.put("color", "red");
        params1.put("upStationId", 1L);
        params1.put("downStationId", 2L);
        params1.put("distance", 10);

        ExtractableResponse<Response> createResponse1 = createLineResponseOf(params1);

        Map<String, Object> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "blue");
        params2.put("upStationId", 1L);
        params2.put("downStationId", 2L);
        params2.put("distance", 10);

        ExtractableResponse<Response> createResponse2 = createLineResponseOf(params2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> createdLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> findAllLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(findAllLineIds).containsAll(createdLineIds);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "red");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);

        ExtractableResponse<Response> createResponse = createLineResponseOf(params);

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

    @DisplayName("노선을 조회한다.")
    @Test
    void searchLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "red");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);

        ExtractableResponse<Response> createResponse = createLineResponseOf(params);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        String expectedLine = createResponse.body().asString();
        String resultLine = response.body().asString();

        assertThat(resultLine).isEqualTo(expectedLine);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void editLine() {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "red");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 10);

        createLineResponseOf(params);

        // when
        Map<String, String> editParams = new HashMap<>();
        editParams.put("name", "2호선");
        editParams.put("color", "blue");

        ExtractableResponse<Response> editResponse = RestAssured.given().log().all()
                .body(editParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();


        ExtractableResponse<Response> findAllResponse = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        // then
        assertThat(editResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findAllResponse.body().asString()).contains("2호선");
        assertThat(findAllResponse.body().asString()).contains("blue");

    }

    private void createStationResponseOf(Map<String, String> params) {
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createLineResponseOf(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
