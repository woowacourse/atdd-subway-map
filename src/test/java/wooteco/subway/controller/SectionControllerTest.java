package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
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
import wooteco.subway.service.LineService;

public class SectionControllerTest extends AcceptanceTest {

    @Autowired
    StationDao stationDao;
    @Autowired
    LineService lineService;

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
}
