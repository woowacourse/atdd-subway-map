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
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.SectionRequest;

class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationDao stationDao;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private JdbcSectionDao sectionDao;

    private Station gangnam;
    private Station nowon;
    private Station jamsil;

    private Line line1;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        gangnam = stationDao.save(new Station("강남"));
        nowon = stationDao.save(new Station("노원"));
        jamsil = stationDao.save(new Station("잠실"));
        line1 = lineDao.save(new Line("1호선", "red"));
        Section section1 = sectionDao.save(new Section(line1.getId(), gangnam.getId(), nowon.getId(), 10));
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
        sectionDao.save(new Section(line1.getId(), jamsil.getId(), gangnam.getId(), 5));

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
