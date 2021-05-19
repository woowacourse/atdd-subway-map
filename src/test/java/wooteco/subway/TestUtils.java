package wooteco.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.domain.Line;
import wooteco.subway.section.controller.dto.SectionRequest;
import wooteco.subway.station.controller.dto.StationRequest;

public class TestUtils {

    public static final Long FIRST_STATION_ID = 1L;
    public static final Long SECOND_STATION_ID = 2L;
    public static final Long THIRD_STATION_ID = 3L;

    public static final StationRequest JAMSIL_STATION_REQUEST = new StationRequest("잠실역");
    public static final StationRequest GANGNAM_STATION_REQUEST = new StationRequest("강남역");
    public static final StationRequest YANGJAE_STATION_REQUEST = new StationRequest("양재역");

    public static final Line LINE_TWO = new Line("2호선", "bg-green-600");
    public static final Line LINE_NEW_BUNDANG = new Line("신분당선", "bg-red-600");

    public static final SectionRequest STATION_ONE_TO_THREE_SECTION_REQUEST = new SectionRequest(
            FIRST_STATION_ID,
            THIRD_STATION_ID,
            50
    );

    public static final SectionRequest STATION_TWO_TO_THREE_SECTION_REQUEST = new SectionRequest(
            SECOND_STATION_ID,
            THIRD_STATION_ID,
            50
    );

    public static final SectionRequest STATION_THREE_TO_ONE_SECTION_REQUEST = new SectionRequest(
            THIRD_STATION_ID,
            FIRST_STATION_ID,
            50
    );

    public static final LineRequest LINE_TWO_REQUEST = new LineRequest(
            LINE_TWO.getName(),
            LINE_TWO.getColor(),
            FIRST_STATION_ID,
            SECOND_STATION_ID,
            100
    );
    public static final LineRequest LINE_NEW_BUNDANG_REQUEST = new LineRequest(
            LINE_NEW_BUNDANG.getName(),
            LINE_NEW_BUNDANG.getColor(),
            SECOND_STATION_ID,
            THIRD_STATION_ID,
            100
    );

    public static final SectionRequest LINE_TWO_YANGJAE_REQUEST = new SectionRequest(
            FIRST_STATION_ID,
            THIRD_STATION_ID,
            50
    );

    public static ExtractableResponse<Response> postStation(final StationRequest stationRequest) {
        return RestAssured
                .given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getStations() {
        return RestAssured
                .given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteStation(final String uri) {
        return RestAssured
                .given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> postLine(final LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getLine(final Long id) {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> getLines() {
        return RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateLine(final String uri, final LineRequest updateRequest) {
        return RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteLine(final String uri) {
        return RestAssured
                .given().log().all()
                .when()
                .delete(uri)
                .then()
                .extract();
    }

    public static ExtractableResponse<Response> postSection(final Long lineId, final SectionRequest sectionRequest) {
        return RestAssured
                .given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteSection(final Long lineId, final Long stationId) {
        return RestAssured
                .given().log().all()
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then()
                .extract();
    }
}
