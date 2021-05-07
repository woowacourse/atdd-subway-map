package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.api.dto.LineResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given
        String lineName = "2호선";
        String lineColor = "green";

        // when
        ExtractableResponse<Response> response = 노선_저장_후_Response(lineName, lineColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선 이름 중복 생성 불가 기능")
    @Test
    void duplicatedLineName() {
        // given
        String lineName = "2호선";
        String lineColor = "green";
        노선_저장_후_Response(lineName, lineColor);

        // when
        String lineName2 = "2호선";
        String lineColor2 = "red";
        ExtractableResponse<Response> response = 노선_저장_후_Response(lineName2, lineColor2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("이미 존재하는 노선 이름입니다.");
    }

    @DisplayName("잘못된 요청값으로 노선 생성 요청시, 예외처리")
    @Test
    void createLineFailByNotValidatedRequest() {
        // given
        String wrongLineName = "2";
        String wrongLineColor = "";

        //when
        ExtractableResponse<Response> response = 노선_저장_후_Response(wrongLineName, wrongLineColor);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
            () -> assertThat(response.body().jsonPath().getString("color"))
                .isEqualTo("노선 색을 지정해야합니다."),
            () -> assertThat(response.body().jsonPath().getString("name"))
                .isEqualTo("노선 이름은 최소 2글자 이상만 가능합니다.")
        );
    }

    @DisplayName("노선 색깔 중복 생성 불가 기능")
    @Test
    void duplicatedLineColor() {
        // given
        String lineName = "2호선";
        String lineColor = "green";
        노선_저장_후_Response(lineName, lineColor);

        // when
        String lineName2 = "3호선";
        String lineColor2 = "green";
        ExtractableResponse<Response> response = 노선_저장_후_Response(lineName2, lineColor2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("이미 존재하는 노선 색깔입니다.");
    }


    @DisplayName("전체 노선을 조회한다")
    @Test
    void getLines() {
        //given
        String lineName = "2호선";
        String lineColor = "green";
        노선_저장_후_Response(lineName, lineColor);

        String lineName2 = "신분당선";
        String lineColor2 = "red";
        노선_저장_후_Response(lineName2, lineColor2);

        //when
        ExtractableResponse<Response> response = 노선_조회_후_Response("/lines");

        //then
        List<String> expectedLineNames = Arrays.asList(lineName, lineName2);
        List<String> resultLineNames = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getName)
            .collect(Collectors.toList());

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineNames).containsAll(expectedLineNames);
    }

    @DisplayName("단일 노선 조회")
    @Test
    void getLine() {
        //given
        String lineName = "2호선";
        String lineColor = "green";
        ExtractableResponse<Response> response1 = 노선_저장_후_Response(lineName, lineColor);

        //when
        Long id = response1.jsonPath().getObject(".", LineResponse.class).getId();
        ExtractableResponse<Response> response = 노선_조회_후_Response("/lines/" + id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo(lineName);
        assertThat(lineResponse.getColor()).isEqualTo(lineColor);
    }

    @DisplayName("노선을 수정하는 기능")
    @Test
    void updateLine() {
        //given
        String lineName = "2호선";
        String lineColor = "green";
        ExtractableResponse<Response> response1 = 노선_저장_후_Response(lineName, lineColor);

        String lineName2 = "3호선";
        String lineColor2 = "orange";
        Map<String, String> params = 노선_저장을_위한_Request_정보(lineName2, lineColor2);

        //when
        Long id = response1.jsonPath().getObject(".", LineResponse.class).getId();
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + id)
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 단일 노선 조회")
    @Test
    void getLineIfNotFoundId() {
        //given
        String lineName = "2호선";
        String lineColor = "green";
        ExtractableResponse<Response> response1 = 노선_저장_후_Response(lineName, lineColor);

        //when
        Long id = response1.jsonPath().getObject(".", LineResponse.class).getId();
        ExtractableResponse<Response> response = 노선_조회_후_Response("/lines/" + (id + 1L));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("존재하지 않는 노선 ID 입니다.");
    }

    @DisplayName("노선 삭제 기능")
    @Test
    void delete() {
        //given
        String lineName = "2호선";
        String lineColor = "green";
        ExtractableResponse<Response> response1 = 노선_저장_후_Response(lineName, lineColor);

        //when
        Long id = response1.body().jsonPath().getObject(".", LineResponse.class).getId();
        ExtractableResponse<Response> response = 노선_삭제_후_Response(id);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 노선 삭제 요청 시, 예외 처리 기능")
    @Test
    void deleteIfNotExistLineId() {
        //given
        String lineName = "2호선";
        String lineColor = "green";
        ExtractableResponse<Response> response1 = 노선_저장_후_Response(lineName, lineColor);

        //when
        Long id = response1.body().jsonPath().getObject(".", LineResponse.class).getId();

        ExtractableResponse<Response> response = 노선_삭제_후_Response(id + 1);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("존재하지 않는 노선 ID 입니다.");
    }

    private ExtractableResponse<Response> 노선_저장_후_Response(String lineName, String lineColor) {
        Map<String, String> params = 노선_저장을_위한_Request_정보(lineName, lineColor);

        // when
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private Map<String, String> 노선_저장을_위한_Request_정보(String lineName, String lineColor) {
        Map<String, String> params = new HashMap<>();
        params.put("color", lineColor);
        params.put("name", lineName);
        return params;
    }

    private ExtractableResponse<Response> 노선_삭제_후_Response(Long id) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_조회_후_Response(String s) {
        return RestAssured.given().log().all()
                .when()
                .get(s)
                .then().log().all()
                .extract();
    }
}
