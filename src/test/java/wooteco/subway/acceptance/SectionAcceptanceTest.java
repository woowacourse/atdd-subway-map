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
import wooteco.subway.repository.dao.StationDao;
import wooteco.subway.repository.entity.StationEntity;
import wooteco.subway.service.LineService;
import wooteco.subway.service.SectionService;
import wooteco.subway.service.dto.LineRequest;
import wooteco.subway.service.dto.LineResponse;
import wooteco.subway.service.dto.SectionRequest;
import wooteco.subway.service.dto.SectionSaveRequest;

class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;

    @Autowired
    private LineService lineService;

    @Autowired
    private SectionService sectionService;

    private StationEntity gangnam;
    private StationEntity nowon;
    private StationEntity jamsil;

    private LineResponse line1;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        gangnam = stationDao.save(new StationEntity(null, "강남"));
        nowon = stationDao.save(new StationEntity(null, "노원"));
        jamsil = stationDao.save(new StationEntity(null, "잠실"));
        line1 = lineService.save(new LineRequest("1호선", "red", gangnam.getId(), nowon.getId(), 10));
    }

    @Test
    @DisplayName("구간 등록하기")
    void save() {
        // given
        SectionRequest request = new SectionRequest(jamsil.getId(), gangnam.getId(), 5);

        // when
        ExtractableResponse<Response> response =
                postWithBody("/lines/" + line1.getId() + "/sections", request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 삭제하기")
    void delete() {
        //given
        sectionService.save(new SectionSaveRequest(line1.getId(), jamsil.getId(), nowon.getId(), 5));

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("stationId", gangnam.getId())
                .when()
                .delete("/lines/" + line1.getId() + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
