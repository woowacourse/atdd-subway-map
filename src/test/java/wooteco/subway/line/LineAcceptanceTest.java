package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dao.LineDaoMemory;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.station.dao.StationDaoCache;

public class LineAcceptanceTest extends AcceptanceTest {

    private static final LineDaoMemory LINE_DAO_MEMORY = new LineDaoMemory();
    private static final StationDaoCache stationDaoCache = new StationDaoCache();

    @AfterEach
    void cleanTestResidue() {
        stationDaoCache.clean();
        LINE_DAO_MEMORY.clean();
    }

    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
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
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-blue-100");
        params.put("name", "1호선");

        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        Map<String, String> duplicateParams = new HashMap<>();
        duplicateParams.put("color", "bg-red-101");
        duplicateParams.put("name", "1호선");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(duplicateParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철선 색으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-404");
        params.put("name", "3호선");

        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        // when
        Map<String, String> duplicateParams = new HashMap<>();
        duplicateParams.put("color", "bg-red-404");
        duplicateParams.put("name", "4호선");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(duplicateParams)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선을 전체 조회한다.")
    @Test
    void checkAllLines() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-purple-404");
        params.put("name", "호남선");

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        params.put("color", "bg-purple-505");
        params.put("name", "백호선");

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        //then
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
        //given
        final String color = "bg-purple-405";
        final String name = "부산선";
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + responseId)
            .then().log().all()
            .extract();

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        //then
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 노선을 조회한다.")
    @Test
    public void voidLine() {
        //given

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + 999)
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("특정 노선을 수정한다.")
    @Test
    public void updateLine() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-white-400");
        params.put("name", "구미선");
        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        //when
        final String color = "bg-purple-406";
        final String name = "대구선";
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("color", color);
        updateParams.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParams)
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
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getColor()).isEqualTo(color);
    }

    @DisplayName("존재하지 않는 노선을 수정한다.")
    @Test
    public void updateVoidLine() {
        //given

        //when
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

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 다른 노선이 사용중인 색깔로 바꾼다")
    @Test
    public void updateLineToExistedLine() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-white-400");
        params.put("name", "구미선");
        ExtractableResponse<Response> requestResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        params.put("color", "bg-white-401");
        params.put("name", "황천선");
        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        //when
        final String color = "bg-white-400";
        final String name = "대구선";
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("color", color);
        updateParams.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(updateParams)
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

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("특정 노선을 삭제한다.")
    @Test
    public void deleteSpecificLine() {
        //given
        final String color = "bg-purple-511";
        final String name = "울산선";
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);
        ExtractableResponse<Response> formResponse = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        long responseId = Long.parseLong(formResponse.header("Location").split("/")[2]);

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/" + responseId)
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 노선을 삭제한다.")
    @Test
    public void deleteVoidLine() {
        //given

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/" + 999)
            .then().log().all()
            .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
