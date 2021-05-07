package wooteco.subway.line.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("classpath:tableInit.sql")
class LineAcceptanceTest extends AcceptanceTest {
    private ExtractableResponse<Response> response;
    private final LineRequest firstLineRequest = new LineRequest("신분당선", "bg-red-600");
    private String url;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        response = RestAssured.given().log().all()
                .body(firstLineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        url = response.header("Location");
    }

    @DisplayName("노선 추가하는데 성공하면 201 created와 생성된 노선 정보를 반환한다")
    @Test
    void createLine() {
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        LineResponse responseBody = response.body().as(LineResponse.class);

        assertThat(responseBody).usingRecursiveComparison()
                .ignoringFields("upStationId", "downStationId", "distance", "id", "stations")
                .isEqualTo(firstLineRequest);
    }

    @DisplayName("전체 노선을 조회하면 저장된 모든 노선들을 반환한다 ")
    @Test
    void getLines() {
        LineRequest secondLineRequest = new LineRequest("2호선", "bg-green-600");
        RestAssured.given().log().all()
                .body(secondLineRequest)
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

        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        List<LineRequest> lineRequests = Arrays.asList(firstLineRequest, secondLineRequest);

        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder()
                .withIgnoredFields("upStationId", "downStationId", "distance", "id", "stations")
                .withIgnoredCollectionOrderInFields()
                .build();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponses).usingRecursiveFieldByFieldElementComparator(configuration).isEqualTo(lineRequests);
    }

    @DisplayName("id를 통해 노선을 조회하면, 해당 노선 정보를 반환한다.")
    @Test
    void getLine() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(url)
                .then().log().all()
                .extract();

        LineResponse expectedLineResponse = LineResponse.toDto(new Line(1L, "bg-red-600", "신분당선"));

        assertThat(getResponse.as(LineResponse.class)).usingRecursiveComparison().
                isEqualTo(expectedLineResponse);
    }

    @DisplayName("id를 통해 노선을 변경하면, payload대로 노선 수정한다")
    @Test
    void updateLine() {
        LineRequest lineUpdateRequest = new LineRequest("구분당선", "bg-blue-600");
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .body(lineUpdateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(url)
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("id를 통해 노선을 삭제하면, payload대로 노선을 삭제한다")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(url)
                .then().log().all()
                .extract();

        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}