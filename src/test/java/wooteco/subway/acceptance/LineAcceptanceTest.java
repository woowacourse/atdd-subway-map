package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    LineDao lineDao;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");

        ExtractableResponse<Response> response = requestPost(params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");

        requestPost(params);

        params.put("color", "분홍색");

        ExtractableResponse<Response> response = requestPost(params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("이미 존재하는 노선입니다.");
    }

    @DisplayName("기존에 존재하는 노선 색상으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");

        requestPost(params);

        params.put("name", "3호선");

        ExtractableResponse<Response> response = requestPost(params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("이미 존재하는 노선입니다.");
    }

    @DisplayName("전체 지하철 노선을 조회한다.")
    @Test
    void getLines() {
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "1호선");
        params1.put("color", "군청색");
        requestPost(params1);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "2호선");
        params2.put("color", "초록색");
        requestPost(params2);

        ExtractableResponse<Response> response = requestGet();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getList(".", LineResponse.class)).hasSize(2);
    }

    @DisplayName("지하철 노선을 id로 조회한다.")
    @Test
    void getLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");
        ExtractableResponse<Response> createResponse = requestPost(params);

        Long id = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = requestGet(id);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        Long actual = response.jsonPath().getObject(".", LineResponse.class).getId();
        assertThat(actual).isEqualTo(id);
    }

    @DisplayName("특정 id를 가지는 노선을 수정한다.")
    @Test
    void updateLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");
        ExtractableResponse<Response> createResponse = requestPost(params);

        Long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "1호선");
        params2.put("color", "군청색");
        RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + id)
                .then().log().all();

        ExtractableResponse<Response> response = requestGet(id);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        String actualName = response.jsonPath().getObject(".", LineResponse.class).getName();
        String actualColor = response.jsonPath().getObject(".", LineResponse.class).getColor();
        assertThat(actualName).isEqualTo("1호선");
        assertThat(actualColor).isEqualTo("군청색");
    }

    @DisplayName("특정 id의 노선을 삭제한다.")
    @Test
    void deleteLine() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "초록색");
        ExtractableResponse<Response> createResponse = requestPost(params);

        long id = Long.parseLong(createResponse.header("Location").split("/")[2]);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + id)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> readResponse = requestGet();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(readResponse.jsonPath().getList(".")).isEmpty();
    }

    @DisplayName("지하철 노선 이름이나 색으로 null 또는 공백이 올 수 없다.")
    @ParameterizedTest
    @CsvSource(value = {",", "'',''", "' ',' '"})
    void notAllowNullOrBlankNameAndColor(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        ExtractableResponse<Response> response = requestPost(params);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).contains("빈 값일 수 없습니다.");
    }

    private ExtractableResponse<Response> requestPost(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGet() {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestGet(Long id) {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines/" + id)
                .then().log().all()
                .extract();
    }

}
