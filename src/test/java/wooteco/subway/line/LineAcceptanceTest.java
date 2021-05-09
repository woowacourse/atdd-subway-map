package wooteco.subway.line;


import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.dto.LineResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private LineDao lineDao;

    private static final Map<String, String> params1 = new HashMap<>();
    private static final Map<String, String> params2 = new HashMap<>();

    static {
        params1.put("name", "신분당선");
        params1.put("color", "bg-red-600");
        params2.put("name", "2호선");
        params2.put("color", "bg-green-600");
    }

    @AfterEach
    void clear() {
        lineDao.clear();
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-600");
    }

    @DisplayName("지하철 노선 이름은 XX선으로 끝나지 않으면 예외처리가 된다.")
    @Test
    void invalidLineName() {
        // given
        Map<String, String> invalidParam = new HashMap<>();
        invalidParam.put("name", "노선은선이라는단어로끝나야함");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(invalidParam)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("지하철 노선 이름이 잘못되었습니다.");
    }

    @DisplayName("중복된 지하철 노선을 생성할 수 없다.")
    @Test
    void cannotCreateDuplicatedLine() {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.body().asString()).isEqualTo("중복된 노선입니다.");
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> createResponse2 = RestAssured.given().log().all()
                .body(params2)
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
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(line -> Long.parseLong(line.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(line -> line.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(params1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

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
}
