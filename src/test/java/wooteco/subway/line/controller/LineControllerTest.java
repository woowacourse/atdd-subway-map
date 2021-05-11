package wooteco.subway.line.controller;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dao.LineDao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.line.controller.LineControllerTestUtils.지하철노선을_생성한다;

@DisplayName("지하철 노선 테스트")
public class LineControllerTest extends AcceptanceTest {
    private static final String TEST_LINE_NAME = "강남노선";
    private static final String TEST_COLOR_NAME = "orange darken-4";
    private static final Long TEST_UP_STATION_ID = 1L;
    private static final Long TEST_DOWN_STATION_ID = 2L;
    private static final int TEST_DISTANCE = 10;
    private static final LineRequest REQUEST_BODY = new LineRequest(TEST_LINE_NAME, TEST_COLOR_NAME, TEST_UP_STATION_ID, TEST_DOWN_STATION_ID, TEST_DISTANCE);

    @Autowired
    private LineDao dao;

    @DisplayName("지하철노선을 생성한다.")
    @Transactional
    @Test
    void createLine() {
        // given
        // when
        ExtractableResponse<Response> response = 지하철노선을_생성한다(REQUEST_BODY);

        final LineResponse lineResponse = response.body().as(LineResponse.class);
        // then
        assertThat(REQUEST_BODY.getName()).isEqualTo(lineResponse.getName());
        assertThat(REQUEST_BODY.getColor()).isEqualTo(lineResponse.getColor());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }



    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Transactional
    @Test
    void createLineWithDuplicateName() {
        // given
        지하철노선을_생성한다(REQUEST_BODY);

        // when
        LineRequest duplicateNameRequest = new LineRequest(TEST_LINE_NAME, "red darken-3", TEST_UP_STATION_ID, TEST_DOWN_STATION_ID, TEST_DISTANCE);

        ExtractableResponse<Response> response = 지하철노선을_생성한다(duplicateNameRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철노선 색상으로 지하철노선을 생성한다.")
    @Transactional
    @Test
    void createLineWithDuplicateColor() {
        // given
        지하철노선을_생성한다(REQUEST_BODY);

        // when
        LineRequest duplicateColorRequest = new LineRequest("다른이름역", TEST_COLOR_NAME, TEST_UP_STATION_ID, TEST_DOWN_STATION_ID, TEST_DISTANCE);

        ExtractableResponse<Response> response = 지하철노선을_생성한다(duplicateColorRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철노선을 id로 조회한다")
    @Transactional
    @Test
    void getLineById() {
        /// given
        final ExtractableResponse<Response> createResponse = 지하철노선을_생성한다(REQUEST_BODY);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo(TEST_LINE_NAME);
        assertThat(lineResponse.getColor()).isEqualTo(TEST_COLOR_NAME);
    }

    @DisplayName("지하철노선을 조회한다.")
    @Transactional
    @Test
    void getLines() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철노선을_생성한다(REQUEST_BODY);

        LineRequest anotherRequestBody = new LineRequest("마이크로소프트호선", "blue darken-4", TEST_UP_STATION_ID, TEST_DOWN_STATION_ID, TEST_DISTANCE);

        ExtractableResponse<Response> createResponse2 = 지하철노선을_생성한다(anotherRequestBody);

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
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("노선 정보를 업데이트한다.")
    @Transactional
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> response = 지하철노선을_생성한다(REQUEST_BODY);

        // when
        String updateName = "빨리빨리노선";
        String updateColor = "red darken-3";

        LineRequest updateRequest = new LineRequest(updateName, updateColor, TEST_UP_STATION_ID, TEST_DOWN_STATION_ID, TEST_DISTANCE);

        String uri = response.header("Location");
        ExtractableResponse<Response> createResponse = RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        ExtractableResponse<Response> updateResponse = RestAssured.given().log().all()
                .when()
                .get(uri)
                .then().log().all()
                .extract();

        LineResponse result = updateResponse.body().as(LineResponse.class);

        // then
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getName()).isEqualTo(updateName);
        assertThat(result.getColor()).isEqualTo(updateColor);
    }

    @DisplayName("지하철노선을 제거한다.")
    @Transactional
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선을_생성한다(REQUEST_BODY);

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

    @DisplayName("없는 ID의 지하철노선을 삭제하려고 하면 예외")
    @Transactional
    @Test
    void whenTryDeleteWrongIdLine() {
        // given
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("lines/-1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}