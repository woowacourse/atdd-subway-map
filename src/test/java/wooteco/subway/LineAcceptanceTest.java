package wooteco.subway;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.request.StationRequest;
import wooteco.subway.controller.dto.request.UpdateLineRequest;
import wooteco.subway.controller.dto.response.LineResponse;
import wooteco.subway.domain.Station;
import wooteco.subway.service.dto.CreateLineDto;

public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", (long) 1, (long) 2, 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-blue-100", (long) 1, (long) 2, 5);
        LineRequest lineRequest2 = new LineRequest("1호선", "bg-red-200", (long) 3, (long) 4, 5);

        RestAssured.given().log().all()
            .body(lineRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("기존에 존재하는 지하철선 색으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-yellow-100", (long) 1, (long) 2, 5);
        LineRequest lineRequest2 = new LineRequest("3호선", "bg-yellow-100", (long) 3, (long) 4, 5);

        RestAssured.given().log().all()
            .body(lineRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(lineRequest2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선을 전체 조회한다.")
    @Test
    void checkAllLines() {
        // given
        LineRequest lineRequest1 = new LineRequest("1호선", "bg-blue-100", (long) 1, (long) 2, 5);
        LineRequest lineRequest2 = new LineRequest("3호선", "bg-yellow-200", (long) 3, (long) 4, 5);

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
            .body(lineRequest1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
            .body(lineRequest2)
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

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(response1, response2).stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(it -> it.getId())
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    public void specificLine() {
        // given
        final String color = "bg-purple-405";
        final String name = "부산선";
        final long upStationId = 1L;
        final long downStationId = 2L;
        final int distance = 10;

        StationRequest guroStationRequest = new StationRequest("구로디지털단지역");
        StationRequest sangBongStationRequest = new StationRequest("상봉역");

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
            .body(guroStationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
            .body(sangBongStationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        //when
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);
        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + responseId)
            .then().log().all()
            .extract();

        System.out.println("오긴오냐?");
        System.out.println(response.statusCode());

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 노선을 조회한다.")
    @Test
    public void voidLine() {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + 999)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    public void updateLine() {
        // given
        StationRequest guroStationRequest = new StationRequest("구로디지털단지역");
        StationRequest sangBongStationRequest = new StationRequest("상봉역");

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
            .body(guroStationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
            .body(sangBongStationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
        CreateLineDto 구미선 = new CreateLineDto("구미선", "bg-blue-100", 1L, 2L, 10);

        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(구미선)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        // when
        final String color = "bg-purple-406";
        final String name = "대구선";

        UpdateLineRequest updateLineRequest = new UpdateLineRequest(name, color);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + responseId)
            .then().log().all()
            .extract();

        ExtractableResponse<Response> checkLineResponse = RestAssured.given().log().all()
            .when()
            .get("/lines/" + responseId)
            .then().log().all()
            .extract();

        LineResponse lineResponse = checkLineResponse.jsonPath().getObject(".", LineResponse.class);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    public void updateVoidLine() {
        // given

        // when
        final String color = "bg-purple-406";
        final String name = "대구선";
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("color", color);
        updateParams.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + 999)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("이미 다른 노선이 사용중인 색깔로 바꾼다")
    @Test
    public void updateLineToExistedLine() {
        // given
        StationRequest guroStationRequest = new StationRequest("구로디지털단지역");
        StationRequest sangBongStationRequest = new StationRequest("상봉역");

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
            .body(guroStationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
            .body(sangBongStationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
        final String color = "bg-purple-401";
        final String name = "구미선";
        final long upStationId = 1L;
        final long downStationId = 2L;
        final int distance = 10;
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId,distance);
        ExtractableResponse<Response> requestResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        final String targetColor = "bg-purple-402";
        final String targetName = "황천선";
        lineRequest = new LineRequest(targetName, targetColor, upStationId, downStationId,distance);

        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        // when

        UpdateLineRequest updateLineRequest = new UpdateLineRequest(targetName, color);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateLineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + responseId)
            .then().log().all()
            .extract();

        ExtractableResponse<Response> checkLineResponse = RestAssured.given().log().all()
            .when()
            .get("/lines/" + responseId)
            .then().log().all()
            .extract();

        LineResponse lineResponse = checkLineResponse.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }


    @DisplayName("특정 노선을 삭제한다.")
    @Test
    public void deleteSpecificLine() {
        // given
        final String color = "bg-purple-511";
        final String name = "울산선";
        final long upStationId = 1L;
        final long downStationId = 2L;
        final int distance = 10;
        LineRequest lineRequest = new LineRequest(color, name, upStationId, downStationId,distance);
        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/" + responseId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 노선 제거요청시 예외처리")
    @Test
    public void deleteVoidLine() {
        // given

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/" + 999)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
