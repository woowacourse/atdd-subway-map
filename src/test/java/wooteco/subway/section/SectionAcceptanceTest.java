package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineRequest;
import wooteco.subway.line.LineResponse;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationRequest;
import wooteco.subway.station.StationResponse;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@Sql("/truncate.sql")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static final StationRequest 강남역 = new StationRequest("강남역");
    private static final StationRequest 역삼역 = new StationRequest("역삼역");
    private static final StationRequest 아차산역 = new StationRequest("아차산역");
    private static final StationRequest 탄현역 = new StationRequest("탄현역");
    private static final StationRequest 일산역 = new StationRequest("일산역");
    private static final StationRequest 홍대입구역 = new StationRequest("홍대입구역");
    private static final StationRequest 판교역 = new StationRequest("판교역");
    private static final StationRequest 정자역 = new StationRequest("정자역");
    private static final StationRequest 교대역 = new StationRequest("교대역");

    private static final LineRequest 이호선 =
            new LineRequest("2호선", "초록색", 1L, 2L, 10);
    private static final LineRequest 경의중앙선 =
            new LineRequest("경의중앙선", "하늘색", 4L, 5L, 8);
    private static final LineRequest 신분당선 =
            new LineRequest("신분당선", "빨간색", 7L, 8L, 12);

    private static final SectionRequest 강남_역삼 = new SectionRequest(1L, 2L, 10);
    private static final SectionRequest 탄현_일산 = new SectionRequest(4L, 5L, 8);
    private static final SectionRequest 일산_홍대입구 = new SectionRequest(5L, 6L, 10);
    private static final SectionRequest 판교_정자 = new SectionRequest(7L, 8L, 12);
    private static final SectionRequest 교대_강남 = new SectionRequest(9L, 1L, 8);
    private static final SectionRequest 역삼_아차산 = new SectionRequest(2L, 3L, 10);

    @BeforeEach
    void initialize() {
        stationResponse(강남역);
        stationResponse(역삼역);
        stationResponse(아차산역);
        stationResponse(탄현역);
        stationResponse(일산역);
        stationResponse(홍대입구역);
        stationResponse(판교역);
        stationResponse(정자역);
        stationResponse(교대역);

        lineResponse(이호선);
        lineResponse(경의중앙선);
        lineResponse(신분당선);

        sectionResponse(1L, 강남_역삼);
        sectionResponse(2L, 탄현_일산);
        sectionResponse(2L, 일산_홍대입구);
        sectionResponse(3L, 판교_정자);
    }

    @DisplayName("구간을 생성한다. - 기존 구간의 상행역을 포함한 경우")
    @Test
    void createSectionWithContainingOriginalUpStation() {
        ExtractableResponse<Response> sectionResponse = sectionResponse(1L, 교대_강남);

        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        findLineForCreateSectionWithContainingOriginalUpStation();
    }

    private void findLineForCreateSectionWithContainingOriginalUpStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        StationResponse 교대역_응답 = new StationResponse(new Station(9L, "교대역"));
        StationResponse 강남역_응답 = new StationResponse(new Station(1L, "강남역"));
        StationResponse 역삼역_응답 = new StationResponse(new Station(2L, "역삼역"));

        findLineForCreateSectionWithStations(response, 교대역_응답, 강남역_응답, 역삼역_응답);
    }

    @DisplayName("구간을 생성한다. - 기존 구간의 하행역을 포함한 경우")
    @Test
    void createSectionWithContainingOriginalDownStation() {
        ExtractableResponse<Response> sectionResponse = sectionResponse(1L, 역삼_아차산);

        assertThat(sectionResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        findLineForCreateSectionWithContainingOriginalDownStation();
    }

    private void findLineForCreateSectionWithContainingOriginalDownStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        StationResponse 강남역_응답 = new StationResponse(new Station(1L, "강남역"));
        StationResponse 역삼역_응답 = new StationResponse(new Station(2L, "역삼역"));
        StationResponse 아차산역_응답 = new StationResponse(new Station(3L, "아차산역"));

        findLineForCreateSectionWithStations(response, 강남역_응답, 역삼역_응답, 아차산역_응답);
    }

    private void findLineForCreateSectionWithStations(ExtractableResponse<Response> response,
                                                      StationResponse stationResponse1,
                                                      StationResponse stationResponse2,
                                                      StationResponse stationResponse3) {
        LineResponse line = new LineResponse(
                new Line(1L, "2호선", "초록색"),
                Arrays.asList(stationResponse1, stationResponse2, stationResponse3));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse result = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(line);
    }

    @DisplayName("구간을 생성할 때 예외가 발생한다. - 노선과 이어지지 않는 구간을 생성하는 경우")
    @Test
    void createDisconnectedSection() {
        SectionRequest 탄현_일산 = new SectionRequest(4L, 5L, 10);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(탄현_일산)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 생성할 때 예외가 발생한다. - 노선에 이미 존재하는 구간을 생성하는 경우")
    @Test
    void createExistingSection() {
        SectionRequest 강남_역삼 = new SectionRequest(1L, 2L, 10);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(강남_역삼)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 생성할 때 예외가 발생한다. - 노선에 동일 길이 구간을 생성하는 경우")
    @Test
    void createSameDistanceSection() {
        SectionRequest 일산_아차산 = new SectionRequest(5L, 3L, 10);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(일산_아차산)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/2/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 생성할 때 예외가 발생한다. - 노선에 초과 길이 구간을 생성하는 경우")
    @Test
    void createLongDistanceSection() {
        SectionRequest 일산_아차산 = new SectionRequest(5L, 3L, 12);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(일산_아차산)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/2/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 삭제한다. - 기존 구간의 상행역인 경우")
    @Test
    void deleteSectionUpStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .queryParam("stationId", 4)
                .delete("/lines/2/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        findLineForDeleteSectionUpStation();
    }

    private void findLineForDeleteSectionUpStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/2")
                .then().log().all()
                .extract();

        StationResponse 일산역_응답 = new StationResponse(new Station(5L, "일산역"));
        StationResponse 홍대입구역_응답 = new StationResponse(new Station(6L, "홍대입구역"));

        findLineForDeleteSectionWithStations(response, 일산역_응답, 홍대입구역_응답);
    }

    @DisplayName("구간을 삭제한다. - 기존 구간의 중간역인 경우")
    @Test
    void deleteSectionIntermediateStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .queryParam("stationId", 5)
                .delete("/lines/2/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        findLineForDeleteSectionIntermediateStation();
    }

    private void findLineForDeleteSectionIntermediateStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/2")
                .then().log().all()
                .extract();

        StationResponse 탄현역_응답 = new StationResponse(new Station(4L, "탄현역"));
        StationResponse 홍대입구역_응답 = new StationResponse(new Station(6L, "홍대입구역"));

        findLineForDeleteSectionWithStations(response, 탄현역_응답, 홍대입구역_응답);
    }

    @DisplayName("구간을 삭제한다. - 기존 구간의 하행역인 경우")
    @Test
    void deleteSectionDownStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .queryParam("stationId", 6)
                .delete("/lines/2/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        findLineForDeleteSectionDownStation();
    }

    private void findLineForDeleteSectionDownStation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/2")
                .then().log().all()
                .extract();

        StationResponse 탄현역_응답 = new StationResponse(new Station(4L, "탄현역"));
        StationResponse 일산역_응답 = new StationResponse(new Station(5L, "일산역"));

        findLineForDeleteSectionWithStations(response, 탄현역_응답, 일산역_응답);
    }

    private void findLineForDeleteSectionWithStations(ExtractableResponse<Response> response,
                                                      StationResponse stationResponse1,
                                                      StationResponse stationResponse2) {
        LineResponse line = new LineResponse(
                new Line(2L, "경의중앙선", "하늘색"),
                Arrays.asList(stationResponse1, stationResponse2));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse result = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(line);
    }

    @DisplayName("구간을 삭제할 때 예외가 발생한다. - 마지막 구간을 삭제하는 경우")
    @Test
    void deleteLastSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .queryParam("stationId", 7)
                .delete("/lines/3/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void stationResponse(StationRequest stationRequest) {
        RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    private void lineResponse(LineRequest lineRequest) {
        RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> sectionResponse(Long lineId, SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }
}
