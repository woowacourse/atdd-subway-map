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
import wooteco.subway.util.JsonAndMapContainsTester;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Transactional
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given, when
        ExtractableResponse<Response> response = createLineRequest(getLineParams());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        JsonAndMapContainsTester.test(response.body().asString(), getLineParams());
    }

//    @DisplayName("기존에 존재하는 노선 이름으로 지하철역을 생성한다.")
//    @Test
//    void createLineWithDuplicateName() {
//        // given
//        createLineResponse(getLineParams());
//
//        // when
//        ExtractableResponse<Response> response = createLineResponse(getLineParams());
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
//    }
//
//    @DisplayName("노선에 등록된 역 목록을 조회한다.")
//    @Test
//    void getLines() {
//        /// given
//        ExtractableResponse<Response> createdResponse1 = createLineResponse(getLineParams());
//        ExtractableResponse<Response> createdResponse2 = createLineResponse(getLineParams2());
//
//        // when
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .when()
//                .get("/lines")
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//        List<Long> expectedLineIds = Arrays.asList(createdResponse1, createdResponse2).stream()
//                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
//                .collect(Collectors.toList());
//        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
//                .map(it -> it.getId())
//                .collect(Collectors.toList());
//        assertThat(resultLineIds).containsAll(expectedLineIds);
//    }
//
//    @DisplayName("노선에 등록된 역을 조회한다.")
//    @Test
//    void getLine() {
//        // given
//        ExtractableResponse<Response> createdResponse = createLineResponse(getLineParams());
//
//        Long expectedLineId = Long.parseLong(createdResponse.header("Location").split("/")[2]);
//
//        // when
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .pathParam("lineId", expectedLineId)
//                .body(getLineParams())
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .get("/lines/{lineId}")
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//        Long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();
//        assertThat(resultLineId).isEqualTo(expectedLineId);
//    }
//
//    @DisplayName("노선을 수정한다.")
//    @Test
//    void updateLine() {
//        // given
//        ExtractableResponse<Response> createdResponse = createLineResponse(getLineParams());
//        Long expectedLineId = Long.parseLong(createdResponse.header("Location").split("/")[2]);
//
//        // when
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .pathParam("lineId", expectedLineId)
//                .body(getLineParams2())
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when()
//                .put("/lines/{lineId}")
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
//    }
//
//    @DisplayName("노선을 제거한다.")
//    @Test
//    void deleteLine() {
//        // given
//        ExtractableResponse<Response> createdResponse = createLineResponse(getLineParams());
//
//        // when
//        String uri = createdResponse.header("Location");
//        ExtractableResponse<Response> response = RestAssured.given().log().all()
//                .when()
//                .delete(uri)
//                .then().log().all()
//                .extract();
//
//        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
//    }

    private ExtractableResponse<Response> createLineRequest(Map<String, String> params) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        return response;
    }

    private Map<String, String> getLineParams() {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "10");
        return params;
    }

//    private Map<String, String> getLineParams2() {
//        Map<String, String> params = new HashMap<>();
//        params.put("color", "bg-green-600");
//        params.put("name", "2호선");
//        return params;
//    }
}