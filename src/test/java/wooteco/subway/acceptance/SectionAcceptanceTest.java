package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.SubwayService;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private SubwayService subwayService;

    @BeforeEach
    void setStationsAndLine() {
        Station station1 = Station.of("잠실역");
        Station station2 = Station.of("강남역");

        stationDao.save(station1);
        stationDao.save(station2);

        LineRequest lineRequest = new LineRequest("2호선", "초록색", 1L, 2L, 10);
        subwayService.addLine(lineRequest);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        Station station = Station.of("선릉역");
        station = stationDao.save(station);
        SectionRequest sectionRequest = new SectionRequest(1L, station.getId(), 3);

        ExtractableResponse<Response> response = requestPost(sectionRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        Station station = Station.of("선릉역");
        station = stationDao.save(station);
        SectionRequest sectionRequest = new SectionRequest(1L, station.getId(), 3);
        requestPost(sectionRequest);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .param("stationId", 3L)
                .when()
                .delete("/lines/1/sections")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> requestPost(SectionRequest sectionRequest) {
        return RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
    }
}
