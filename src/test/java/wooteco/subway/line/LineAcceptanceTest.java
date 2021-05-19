package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.station.StationAcceptanceTest.지하철역_등록;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.section.SectionRequest;
import wooteco.subway.line.section.SectionResponse;
import wooteco.subway.station.StationRequest;

@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private final LineRequest LINE_2 = new LineRequest(
        "2호선", "grey darken-1", 1L, 2L, 2, 500
    );

    private final LineRequest LINE_3 = new LineRequest(
        "3호선", "grey darken-2", 5L, 6L, 12, 1500
    );

    private ExtractableResponse<Response> 노선_등록(final LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        지하철역_등록(new StationRequest("강남역1"));
        지하철역_등록(new StationRequest("강남역2"));
        지하철역_등록(new StationRequest("강남역3"));
        지하철역_등록(new StationRequest("강남역4"));
        지하철역_등록(new StationRequest("강남역5"));
        지하철역_등록(new StationRequest("강남역6"));
    }

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        final ExtractableResponse<Response> response = 노선_등록(LINE_2);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        노선_생성값_검증(response, LINE_2);
    }

    private void 노선_생성값_검증(final ExtractableResponse<Response> response, final LineRequest lineRequest) {
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getId()).isEqualTo(getCreatedId(response));
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }

    private void 노선_생성값_검증(final ExtractableResponse<Response> response, final LineRequest lineRequest,
        final Long createdId) {
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        assertThat(lineResponse.getId()).isEqualTo(createdId);
        assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName());
        assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor());
    }

    private long getCreatedId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }

    @DisplayName("기존에 존재하는 노선의 이름으로 노선을 생성하면 400 에러가 발생한다.")
    @Test
    void createLineWithDuplicateName() {
        노선_등록(LINE_2);

        final ExtractableResponse<Response> response = 노선_등록(LINE_2);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void showLines() {
        final ExtractableResponse<Response> createdResponse1 = 노선_등록(LINE_2);
        final ExtractableResponse<Response> createdResponse2 = 노선_등록(LINE_3);

        final ExtractableResponse<Response> response = 노선_조회();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<Long> expectedLineIds = makeExpectedLineIds(Arrays.asList(createdResponse1, createdResponse2));
        final List<Long> resultLineIds = makeResultLineIds(response);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private List<Long> makeExpectedLineIds(final List<ExtractableResponse<Response>> responses) {
        return responses.stream()
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
    }

    private List<Long> makeResultLineIds(final ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
    }

    private ExtractableResponse<Response> 노선_조회() {
        return RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void showLine() {
        final ExtractableResponse<Response> createResponse = 노선_등록(LINE_2);
        final Long createdId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        final ExtractableResponse<Response> response = 노선_조회(createdId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        노선_생성값_검증(response, LINE_2, createdId);
    }

    private ExtractableResponse<Response> 노선_조회(final Long createId) {
        return RestAssured.given().log().all()
            .when()
            .get("/lines/" + createId)
            .then().log().all()
            .extract();
    }

    @DisplayName("없는 노선을 조회하면 404 에러가 발생한다.")
    @Test
    void showNotExistLine() {
        final ExtractableResponse<Response> response = 노선_조회(2000000L);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        final ExtractableResponse<Response> createResponse = 노선_등록(LINE_2);
        final String uri = createResponse.header("Location");

        final LineRequest updatedRequest = new LineRequest("3호선", "grey darken-2", 1L, 2L, 10, 100);
        final ExtractableResponse<Response> response = 노선_수정(uri, updatedRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 노선_수정(final String uri, final LineRequest updatedRequest) {
        return RestAssured.given().log().all()
            .body(updatedRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();
    }

    @DisplayName("없는 노선을 수정하면 404 에러가 발생한다.")
    @Test
    void updateNotExistLine() {
        final LineRequest updatedRequest = new LineRequest("3호선", "grey darken-2", 1L, 2L, 10, 100);
        final ExtractableResponse<Response> response = 노선_수정("/lines/2000000", updatedRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        final ExtractableResponse<Response> createResponse = 노선_등록(LINE_2);
        final String uri = createResponse.header("Location");

        final ExtractableResponse<Response> response = 노선_제거(uri);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 노선_제거(final String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }

    @DisplayName("없는 노선을 제거하면 404 에러가 발생한다.")
    @Test
    void deleteNotExistLine() {
        final ExtractableResponse<Response> response = 노선_제거("/lines/2000000");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        final String uri = 노선_등록(LINE_2).header("Location") + "/sections";
        final SectionRequest sectionRequest = new SectionRequest(2L, 4L, 10);
        final ExtractableResponse<Response> response = 구간_등록(uri, sectionRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        구간_생성값_검증(response, sectionRequest);
    }

    @DisplayName("상행과 하행역의 이름이 같으면 BAD REQUEST 응답이 발생한다.")
    @Test
    void createSection_fail_duplicatedStationName() {
        final String uri = 노선_등록(LINE_2).header("Location") + "/sections";
        final SectionRequest sectionRequest = new SectionRequest(2L, 2L, 10);
        final ExtractableResponse<Response> response = 구간_등록(uri, sectionRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 구간_등록(final String uri, final SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(uri)
            .then().log().all()
            .extract();
    }

    private void 구간_생성값_검증(final ExtractableResponse<Response> response, final SectionRequest sectionRequest) {
        final SectionResponse sectionResponse = response.body().as(SectionResponse.class);
        assertThat(sectionResponse.getId()).isEqualTo(getCreatedSectionId(response));
        assertThat(sectionResponse.getDownStationId()).isEqualTo(sectionRequest.getDownStationId());
        assertThat(sectionResponse.getUpStationId()).isEqualTo(sectionRequest.getUpStationId());
        assertThat(sectionResponse.getDistance()).isEqualTo(sectionRequest.getDistance());
    }

    private long getCreatedSectionId(final ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[4]);
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        final String uri = 노선_등록(LINE_2).header("Location") + "/sections";
        final SectionRequest leftSectionRequest = new SectionRequest(2L, 4L, 10);
        final SectionRequest rightSectionRequest = new SectionRequest(4L, 6L, 10);
        구간_등록(uri, leftSectionRequest);
        구간_등록(uri, rightSectionRequest);

        final ExtractableResponse<Response> response = 구간_제거(String.format("%s?stationId=%s", uri, 4L));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 구간_제거(final String uri) {
        return RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();
    }
}
