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
}
