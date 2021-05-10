package wooteco.subway.line.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("classpath:tableInit.sql")
class LineControllerTest extends AcceptanceTest {
    private ExtractableResponse<Response> response;
    private Map<String, Object> params;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        final ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "역삼역");
        final ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", 1L);
        params.put("downStationId", 2L);
        params.put("distance", 77);
        response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @DisplayName("노선 추가하는데 성공하면 201 created와 생성된 노선 정보를 반환한다")
    @Test
    void createLine() {
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        assertThat(response.response().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.response().jsonPath().getLong("id")).isEqualTo(1L);
        assertThat(response.as(LineResponse.class).getName()).isEqualTo("신분당선");
    }

    @DisplayName("필드값이 없는 Request를 보내면, 400 상태 코드를 받는다.")
    @Test
    void emptyFieldFail() {
        Map<String, Object> wrongParams = new HashMap<>();
        wrongParams.put("color", "bg-red-600");
        wrongParams.put("name", "잘못된 분당선");

        response = RestAssured.given().log().all()
                .body(wrongParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행선과 하행선이 동일하게 노선을 생성하면, 400 상태 코드를 받는다.")
    @Test
    void duplicateStationFail() {
        Map<String, Object> wrongParams = new HashMap<>();
        wrongParams.put("color", "bg-red-600");
        wrongParams.put("name", "잘못된 분당선");
        wrongParams.put("upStationId", 1L);
        wrongParams.put("downStationId", 1L);
        wrongParams.put("distance", 77);

        response = RestAssured.given().log().all()
                .body(wrongParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("없는 station들을 가지고 노선을 생성하면, 404 상태코드를 받는다.")
    @Test
    void createLineNotExistStation() {

        Map<String, Object> wrongParams = new HashMap<>();
        wrongParams.put("color", "bg-red-600");
        wrongParams.put("name", "분당선");
        wrongParams.put("upStationId", 100L);
        wrongParams.put("downStationId", 200L);
        wrongParams.put("distance", 77);

        // then
        response = RestAssured.given().log().all()
                .body(wrongParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("중복된 이름을 가진 노선을 추가하는것을 시도하면, 409 conflict 상태코드를 받는다.")
    @Test
    void createLineFail() {
        // then
        response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("전체 노선을 조회하면 저장된 모든 노선들을 반환한다 ")
    @Test
    void getLines() {
        Map<String, Object> params1 = new HashMap<>();
        params1.put("color", "bg-green-600");
        params1.put("name", "2호선");
        params1.put("upStationId", 1L);
        params1.put("downStationId", 2L);
        params1.put("distance", 77);

        RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        Map<String, Object> sinBunDangLine = (Map<String, Object>) response.as(ArrayList.class).get(0);
        Map<String, Object> line2 = (Map<String, Object>) response.as(ArrayList.class).get(1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(sinBunDangLine.get("name")).isEqualTo("신분당선");
        assertThat(line2.get("name")).isEqualTo("2호선");
    }

    @DisplayName("id를 통해 노선을 조회하면, 해당 노선 정보와 담고 있는 stations List를 반환한다.")
    @Test
    void getLineWithStationResponses() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        List<StationResponse> expectedStationResponses = Arrays.asList(new StationResponse(1L,"강남역"), new StationResponse(2L, "역삼역"));
        LineResponse expectedLineResponse = new LineResponse(
                1L,
                "신분당선",
                "bg-red-600",
                expectedStationResponses
        );
        assertThat(getResponse.as(LineResponse.class)).usingRecursiveComparison().
                isEqualTo(expectedLineResponse);
    }

    @DisplayName("잘못된 id를 통해 노선을 조회하면, 404 상태 코드를 받는다.")
    @Test
    void getLineByWrongId() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/100")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("id를 통해 노선을 변경하면, payload대로 노선 수정한다")
    @Test
    void updateLine() {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");

        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/1")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("잘못된 id를 통해 노선을 변경하면, 404 상태 코드를 받는다.")
    @Test
    void updateLineByWrongId() {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-600");
        params.put("name", "구분당선");

        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/100")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("id를 통해 노선을 삭제하면, payload대로 노선을 삭제한다")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/1")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("잘못된 id를 통해 노선을 삭제하면, 404 상태 코드를 받는다.")
    @Test
    void deleteLineByWrongId() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/100")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("구간 추가시, 존재하지 않는 LineId에 접근하면 404 상태 코드를 받는다.")
    @Test
    void invalidLineIdSectionAdd() {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/100/sections")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("구간 추가시, 상행선과 하행선이 같게 요청을 보내면 400 상태 코드를 받는다.")
    @Test
    void invalidStationsSectionAdd() {
        SectionRequest sectionRequest = new SectionRequest(1L, 1L, 10);
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 추가시, 상행선과 하행선이 둘다 노선에 이미 있으면, 409 상태 코드를 받는다.")
    @Test
    void duplicateSectionAdd() {
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("구간 추가시, 상행선과 하행선이 둘다 노선에 없으면, 404 상태 코드를 받는다.")
    @Test
    void notFoundSectionAdd() {
        SectionRequest sectionRequest = new SectionRequest(3L, 4L, 10);
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

//    @DisplayName("구간 추가시, 존재하는 LineId에 접근하면 200 상태 코드를 받는다.")
//    @Test
//    void validLineIdSectionAdd() {
//        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
//        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
//                .body(sectionRequest)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .post("/lines/1/sections")
//                .then().log().all()
//                .extract();
//
//        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
//    }
}
