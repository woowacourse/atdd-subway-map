package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

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
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String LINE_PARAM_NAME_KEY = "name";
    private static final String LINE_PARAM_COLOR_KEY = "color";

    @DisplayName("지하철 노선을 생성 성공 시 상태코드 201을 반환하고 Location 헤더에 주소를 전달한다.")
    @Test
    void createLine() {
        // given
        ExtractableResponse<Response> response = createLineFixture("2호선", "GREEN");
        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("GREEN");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성 시 상태코드 400을 반환한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        ExtractableResponse<Response> createResponse1 = createLineFixture("2호선", "GREEN");
        // when
        ExtractableResponse<Response> createResponse2 = createLineFixture("2호선", "GREEN");
        // then
        assertThat(createResponse2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회 시 상태코드 200을 반환하고 노선 목록을 반환한다.")
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = createLineFixture("2호선", "GREEN");
        ExtractableResponse<Response> createResponse2 = createLineFixture("3호선", "ORANGE");
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
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선 id를 통해 지하철 노선을 조회하며 성공 시 노선과 상태코드 200을 반환한다.")
    @Test
    void getLineById() {
        /// given
        ExtractableResponse<Response> createdResponse = createLineFixture("2호선", "GREEN");
        String uri = createdResponse.header("Location");
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .get(uri)
            .then().log().all()
            .extract();
        // then
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("GREEN");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선을 수정 성공 시 상태 코드 200을 반환한다.")
    @Test
    void updateLine() {
        /// given
        ExtractableResponse<Response> createdResponse = createLineFixture("2호선", "GREEN");
        String uri = createdResponse.header("Location");

        Map<String, String> updateParameter = generateLineParameter("3호선", "ORANGE");
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParameter)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .put(uri)
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 ID의 노선을 수정 상태 코드 400을 반환한다..")
    @Test
    void updateLine_noExistLine_Exception() {
        /// given
        ExtractableResponse<Response> createdResponse1 = createLineFixture("2호선", "GREEN");
        ExtractableResponse<Response> createdResponse2 = createLineFixture("3호선", "ORANGE");
        Map<String, String> updateParam = generateLineParameter("4호선", "ORANGE");
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .put("/lines/10000")
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중복된 이름으로 노선 수정 시 상태 코드 400을 반환한다.")
    @Test
    void updateLine_duplicateName_Exception() {
        /// given
        ExtractableResponse<Response> createdResponse1 = createLineFixture("2호선", "GREEN");
        ExtractableResponse<Response> createdResponse2 = createLineFixture("3호선", "ORANGE");

        String uri = createdResponse2.header("Location");

        Map<String, String> updateParameter = generateLineParameter("2호선", "BLUE");
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParameter)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .put(uri)
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    //TODO: 중복 색깔 테스트 필요
    @DisplayName("중복된 색깔로 노선 수정 시 상태 코드 400을 반환한다.")
    @Test
    void updateLine_duplicateColor_Exception() {
        /// given
        ExtractableResponse<Response> createdResponse1 = createLineFixture("2호선", "GREEN");
        ExtractableResponse<Response> createdResponse2 = createLineFixture("3호선", "ORANGE");

        String uri = createdResponse2.header("Location");

        Map<String, String> updateParameter = generateLineParameter("4호선", "GREEN");
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParameter)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .put(uri)
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 제거 성공 시 상태 코드 204를 반환한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineFixture("2호선", "GREEN");
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

    @DisplayName("존재하지 않는 ID의 노선을 제거 시 상태 코드 200을 반환한다.")
    @Test
    void deleteLine_noExistLine_Exception() {
        /// given & when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1000")
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Map<String, String> generateLineParameter(String name, String color) {
        Map<String, String> lineParameter = new HashMap<>();
        lineParameter.put(LINE_PARAM_NAME_KEY, name);
        lineParameter.put(LINE_PARAM_COLOR_KEY, color);
        return lineParameter;
    }

    private ExtractableResponse<Response> createLineFixture(String name, String color) {
        Map<String, String> params = generateLineParameter(name, color);
        // when
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }
}
