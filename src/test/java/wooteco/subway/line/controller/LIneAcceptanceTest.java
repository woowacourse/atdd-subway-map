package wooteco.subway.line.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선역 관련 기능")
public class LIneAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("노선을 생성한다.")
    @Test
    void createStation() throws JsonProcessingException {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        String content = objectMapper.writeValueAsString(lineRequest);

        // when
        ExtractableResponse<Response> response = addLine(content);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private ExtractableResponse<Response> addLine(String content) {
        return RestAssured.given().log().all()
                .body(content)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    @DisplayName("노선을 생성 시 필요한 정보가 빠졌을 경우 예외가 발생한다.")
    @Test
    void createStationException1() throws JsonProcessingException {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600");
        String content = objectMapper.writeValueAsString(lineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(content)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 노선역 이름으로 노선을 생성한다.")
    @Test
    void createStationWithDuplicateName() throws JsonProcessingException {
        // given
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        String content = objectMapper.writeValueAsString(lineRequest);

        addLine(content);

        // when
        ExtractableResponse<Response> response = addLine(content);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLines() throws JsonProcessingException {
        /// given
        LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        String content1 = objectMapper.writeValueAsString(lineRequest1);

        ExtractableResponse<Response> createResponse1 = addLine(content1);

        LineRequest lineRequest2 = new LineRequest("백기선", "bg-red-600", 1L, 2L, 10);
        String content2 = objectMapper.writeValueAsString(lineRequest2);

        ExtractableResponse<Response> createResponse2 = addLine(content2);

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


    @DisplayName("단일 노선을 조회한다.")
    @Test
    void findLineByID() {
        // given
        String name = "9호선";
        String color = "bg-red-600";
        Long id = 1L;

        // when
        ExtractableResponse<Response> findLineResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();

        // then
        assertThat(findLineResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findLineResponse.jsonPath().getLong("id")).isEqualTo(id);
        assertThat(findLineResponse.jsonPath().getString("name")).isEqualTo(name);
        assertThat(findLineResponse.jsonPath().getString("color")).isEqualTo(color);
        assertThat(findLineResponse.jsonPath().getList("stations")).hasSize(2);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() throws JsonProcessingException {
        // given
        LineRequest lineRequest1 = new LineRequest("백기선", "bg-red-600", 1L, 2L, 10);
        String content1 = objectMapper.writeValueAsString(lineRequest1);

        // given
        LineRequest lineRequest2 = new LineRequest("흑기선", "bg-red-600");
        String content2 = objectMapper.writeValueAsString(lineRequest2);

        // when
        ExtractableResponse<Response> response1 = addLine(content1);

        long newId = response1.body().jsonPath().getLong("id");

        ExtractableResponse<Response> response2 = RestAssured.given().log().all()
                .body(content2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", newId)
                .then().log().all()
                .extract();

        // then
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    @DisplayName("노선을 수정시 필요한 정보가 빠졌을 경우 예외가 발생한다.")
    @Test
    void updateLineException() throws JsonProcessingException {
        // given
        LineRequest lineRequest = new LineRequest("흑기선", null);
        String content = objectMapper.writeValueAsString(lineRequest);

        // when
        long dummyId = 1L;

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(content)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", dummyId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() throws JsonProcessingException {
        // given
        LineRequest lineRequest = new LineRequest("백기선", "bg-red-600", 1L, 2L, 3);
        String content = objectMapper.writeValueAsString(lineRequest);

        // when
        ExtractableResponse<Response> createResponse = addLine(content);

        String uri = createResponse.header("Location");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


    @DisplayName("노선 제거시 없는 노선이면 예외가 발생한다.")
    @Test
    void deleteStation() {
        String uri = "/lines/{id}";

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri, 0L)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 제거할 시 구간이 하나만 존재하면 예외가 발생한다..")
    @Test
    void deleteSection() throws JsonProcessingException {
        // given
        LineRequest lineRequest = new LineRequest("백기선", "bg-red-600", 1L, 2L, 3);
        String content = objectMapper.writeValueAsString(lineRequest);
        String uri = "/lines/{id}/sections";

        // when
        ExtractableResponse<Response> response =
                RestAssured.given()
                        .param("stationId", 1L)
                        .log().all()
                        .when()
                        .delete(uri, 1L)
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행 구간을 추가한다.")
    void addSection1() throws JsonProcessingException {
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 3);
        String content = objectMapper.writeValueAsString(sectionRequest);

        ExtractableResponse<Response> response = addSection(content);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("하행 구간을 추가한다.")
    void addSection2() throws JsonProcessingException {
        SectionRequest sectionRequest = new SectionRequest(3L, 1L, 3);
        String content = objectMapper.writeValueAsString(sectionRequest);

        ExtractableResponse<Response> response = addSection(content);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> addSection(String content) {
        return RestAssured.given().log().all()
                .body(content)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", 1L)
                .then().log().all()
                .extract();
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection1() throws JsonProcessingException {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 3L, 3);
        String content = objectMapper.writeValueAsString(sectionRequest);
        addSection(content);

        // when
        String uri = "lines/{id}/sections";
        ExtractableResponse<Response> response =
                RestAssured.given()
                        .param("stationId", 1L)
                        .log().all()
                        .when()
                        .delete(uri, 1L)
                        .then().log().all()
                        .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
