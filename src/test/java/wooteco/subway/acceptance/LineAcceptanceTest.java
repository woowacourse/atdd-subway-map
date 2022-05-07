package wooteco.subway.acceptance;

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
import wooteco.subway.dto.LineResponse;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        ExtractableResponse<Response> response = addLine("신분당선", "bg-red-600");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선 생성 시도 시 Bad request가 응답된다.")
    @Test
    void createLineWithDuplicateName() {
        addLine("강남역", "bg-red-600");
        ExtractableResponse<Response> response = addLine("강남역", "bg-red-600");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        ExtractableResponse<Response> createResponse1 = addLine("1호선", "bg-red-600");
        ExtractableResponse<Response> createResponse2 = addLine("2호선", "bg-green-600");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> createResponse = addLine("1호선", "bg-red-600");

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

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void getLine() {
        ExtractableResponse<Response> createResponse = addLine("1호선", "bg-red-600");

        final LineResponse expected = createResponse.jsonPath().getObject(".", LineResponse.class);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(createResponse.header("Location"))
                .then().log().all()
                .extract();
        final LineResponse actual = createResponse.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(actual.getId()).isEqualTo(expected.getId());
    }

    @DisplayName("노선을 갱신한다.")
    @Test
    void updateLine() {
        ExtractableResponse<Response> createResponse = addLine("1호선", "bg-red-600");

        final LineResponse expected = createResponse.jsonPath().getObject(".", LineResponse.class);

        // when
        Map<String, String> newParams = new HashMap<>();
        final String newLineName = "11호선";
        newParams.put("name", newLineName);
        newParams.put("color", "bg-red-600");
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(newParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> updatedResponse = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();
        final LineResponse actual = updatedResponse.jsonPath().getObject(".", LineResponse.class);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(expected.getId()),
                () -> assertThat(actual.getName()).isEqualTo(newLineName),
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
        );
    }

    private ExtractableResponse<Response> addLine(final String name, final String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

}
