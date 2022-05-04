package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.dto.LineResponse;

public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("노선을 생성한다.")
    public void createLine() {
        // given
        Map<String, String> body =
            Map.of("name", "신분당선", "color", "bg-red-600");
        // when
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(body).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
        // then
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
            () -> assertThat(response.header("Location")).isNotBlank()
        );
    }

    @Test
    @DisplayName("전체 노선 목록을 조회한다.")
    void getLines() {
        /// given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        ExtractableResponse<Response> createResponse1 = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();

        Map<String, String> params2 = Map.of("name", "경의중앙선", "color", "bg-red-800");
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
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        Assertions.assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(resultLineIds).containsAll(expectedLineIds)
        );
    }

    @Test
    @DisplayName("ID값으로 노선을 조회한다.")
    public void getLine() {
        // given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        ExtractableResponse<Response> createdResponse = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // when
        final String uri = createdResponse.header("Location");
        final ExtractableResponse<Response> foundResponse = RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();

        final LineResponse createdLineResponse = createdResponse.jsonPath().getObject(".", LineResponse.class);
        final LineResponse foundLineResponse = foundResponse.jsonPath().getObject(".", LineResponse.class);
        // then
        Assertions.assertAll(
            () -> assertThat(foundResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(foundLineResponse.getId()).isEqualTo(createdLineResponse.getId())
        );

    }

    @Test
    @DisplayName("ID값으로 노선을 수정한다.")
    public void modifyLine() {
        // given
        Map<String, String> params1 = Map.of("name", "신분당선", "color", "bg-red-600");
        ExtractableResponse<Response> createdResponse = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        // when
        final Map<String, String> modificationParam =
            Map.of("name", "구분당선", "color", "bg-red-800");
        final String uri = createdResponse.header("Location");
        final ExtractableResponse<Response> modifiedResponse = RestAssured.given().log().all()
            .body(modificationParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(modifiedResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

}
