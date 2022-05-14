package wooteco.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.response.StationResponseDto;
import wooteco.subway.mockDao.MockStationDao;
import wooteco.subway.repository.entity.StationEntity;

public class Fixture {

    private static final MockStationDao stationDao = new MockStationDao();

    public static ExtractableResponse<Response> createLineRequest(final Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> createStationRequest(final Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static Long saveStation(final String name) {
        final Station station = Station.createWithoutId(name);
        final StationEntity saved = stationDao.save(new StationEntity(station));
        return saved.getId();
    }

    public static List<Long> save2StationsRequest(final String stationName1, final String stationName2) {
        Map<String, String> stationParam1 = new HashMap<>();
        stationParam1.put("name", stationName1);
        final StationResponseDto station1 = createStationRequest(stationParam1).jsonPath()
                .getObject(".", StationResponseDto.class);

        Map<String, String> stationParam2 = new HashMap<>();
        stationParam2.put("name", stationName2);
        final StationResponseDto station2 = createStationRequest(stationParam2).jsonPath()
                .getObject(".", StationResponseDto.class);

        return List.of(station1.getId(), station2.getId());
    }

    public static ExtractableResponse<Response> createSectionRequest(final Long lineId,
                                                                     final Map<String, String> params) {
        return RestAssured.given().log().all()
                .when()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteSectionRequest(final Long lineId, final Long newStationId) {
        return RestAssured.given().log().all()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .delete("/lines/" + lineId + "/sections?stationId=" + newStationId)
                .then().log().all()
                .extract();
    }

    public static Map<String, String> makeLineTwoCreationParams(final Long upStationId, final Long downStationId) {
        final Map<String, String> lineParams1 = new HashMap<>();
        lineParams1.put("name", "2호선");
        lineParams1.put("color", "bg-green-600");
        lineParams1.put("upStationId", upStationId.toString());
        lineParams1.put("downStationId", downStationId.toString());
        lineParams1.put("distance", "10");
        return lineParams1;
    }

    public static Map<String, String> makeLineSinBunDangCreationParams(final Long upStationId,
                                                                       final Long downStationId) {
        final Map<String, String> lineParams2 = new HashMap<>();
        lineParams2.put("name", "신분당선");
        lineParams2.put("color", "bg-red-600");
        lineParams2.put("upStationId", upStationId.toString());
        lineParams2.put("downStationId", downStationId.toString());
        lineParams2.put("distance", "10");
        return lineParams2;
    }

    public static ExtractableResponse<Response> deleteLineRequest(final String uri) {
        return RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateLineRequest(final String uri, final Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }
}
