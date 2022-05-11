package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

public class SectionAcceptanceTest extends AcceptanceTest {

    private static final LineRequest lineRequest1 = new LineRequest("2호선", "GREEN", 1L, 2L, 20);
    private static final LineRequest lineRequest2 = new LineRequest("3호선", "ORANGE", 2L, 3L, 20);

    private static final StationRequest STATION_REQUEST_1 = new StationRequest("신설동역");
    private static final StationRequest STATION_REQUEST_2 = new StationRequest("용두역");
    private static final StationRequest STATION_REQUEST_3 = new StationRequest("신답역");
    private static final StationRequest STATION_REQUEST_4 = new StationRequest("성수역");

    @BeforeEach
    void setup() {
        createStationAssured(STATION_REQUEST_1);
        createStationAssured(STATION_REQUEST_2);
        createStationAssured(STATION_REQUEST_3);
        createStationAssured(STATION_REQUEST_4);
    }

    @DisplayName("구간 등록 성공 시 상태코드 200을 반환한다.")
    @Test
    void addSection() {
        // given
        createLineAssured(lineRequest1);

        SectionRequest firstSection = new SectionRequest(2L, 4L, 10);
        ExtractableResponse<Response> response = addSectionAssured(firstSection);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("등록하려는 구간의 상,하행 지하철역이 노선 구간 목록에 이미 모두 존재한다면 상태코드 400을 반환한다.")
    @Test
    void addSection_duplicate_station_id() {
        // given
        createLineAssured(lineRequest1);
        // when
        SectionRequest firstSection = new SectionRequest(1L, 2L, 10);
        ExtractableResponse<Response> response = addSectionAssured(firstSection);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("등록하려는 구간의 상,하행 지하철역이 노선 구간 목록에 없다면 상태코드 400을 반환한다.")
    @Test
    void addSection_no_exist_station_id() {
        // given
        createLineAssured(lineRequest1);
        // when
        SectionRequest firstSection = new SectionRequest(3L, 4L, 10);
        ExtractableResponse<Response> response = addSectionAssured(firstSection);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 구간에 삽입할 때 기존 구간의 길이보다 크거나 같다면 상태코드 400을 반환한다.")
    @Test
    void addSection_in_line_distance_exception() {
        // given
        createLineAssured(lineRequest1);
        // when
        SectionRequest firstSection = new SectionRequest(1L, 3L, 20);
        ExtractableResponse<Response> response = addSectionAssured(firstSection);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 삭제 성공 시 상태코드 200을 반환한다.")
    @Test
    void deleteSection() {
        // given
        createLineAssured(lineRequest1);
        SectionRequest firstSection = new SectionRequest(2L, 4L, 10);
        addSectionAssured(firstSection);
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1/sections?stationId=4")
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간이 1개 존재하는 경우 삭제 시 상태코드 400을 반환한다.")
    @Test
    void deleteSection_minimum_size_exception() {
        // given
        createLineAssured(lineRequest1);
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/1/sections?stationId=2")
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> addSectionAssured(SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/1/sections")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> createStationAssured(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> createLineAssured(LineRequest lineRequest) {
        return RestAssured.given().log().all()
            .body(lineRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }
}
