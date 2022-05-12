package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;

public class SectionControllerTest extends AcceptanceTest {

    @Autowired
    StationDao stationDao;
    @Autowired
    LineService lineService;
    @Autowired
    SectionService sectionService;

    //    String name, String color, Long upStationId, Long downStationId, int distance
    @DisplayName("구간을 등록한다.")
    @Test
    void createSection() {
        Station 동천역 = stationDao.save(new Station("동천역"));
        Station 정자역 = stationDao.save(new Station("정자역"));
        Station 판교역 = stationDao.save(new Station("판교역"));
        LineResponse lineResponse = lineService.save(new LineRequest("신분당선", "red", 판교역.getId(),
                동천역.getId(), 20));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 정자역.getId());
        params.put("downStationId", 동천역.getId());
        params.put("distance", 10);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineResponse.getId() + "/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void delete() {
        Station 동천역 = stationDao.save(new Station("동천역"));
        Station 정자역 = stationDao.save(new Station("정자역"));
        Station 판교역 = stationDao.save(new Station("판교역"));
        Station 미금역 = stationDao.save(new Station("미금역"));
        LineResponse lineResponse = lineService.save(new LineRequest("신분당선", "red", 판교역.getId(),
                동천역.getId(), 20));
        sectionService.save(new SectionRequest(판교역.getId(), 정자역.getId(), 5), lineResponse.getId());
        sectionService.save(new SectionRequest(정자역.getId(), 미금역.getId(), 5), lineResponse.getId());

        Map<String, Object> params = new HashMap<>();
        params.put("stationId", 정자역.getId());

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .params(params)
                .delete("/lines/" + lineResponse.getId() + "/sections")
                .then().log().all()
                .extract();
        List<StationResponse> allStationResponseByLineId = lineService.findAllStationResponseByLineId(
                lineResponse.getId());
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(allStationResponseByLineId).hasSize(3)
        );
    }
}
