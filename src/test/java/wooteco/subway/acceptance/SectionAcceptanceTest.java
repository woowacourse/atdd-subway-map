package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.JdbcLineDao;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.dao.JdbcStationDao;
import wooteco.subway.domain.Section;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private JdbcStationDao jdbcStationDao;
    private JdbcLineDao jdbcLineDao;
    private JdbcSectionDao jdbcSectionDao;

    private String lineId;
    private String upStationId;
    private String downStationId;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void set() {
        jdbcStationDao = new JdbcStationDao(jdbcTemplate);
        jdbcLineDao = new JdbcLineDao(jdbcTemplate);
        jdbcSectionDao = new JdbcSectionDao(jdbcTemplate);

        Long setUpStationId = jdbcStationDao.save("강남역");
        Long setDownStationId = jdbcStationDao.save("역삼역");
        Long setLineId = jdbcLineDao.save("2호선", "green");
        jdbcSectionDao.save(new Section(setLineId, setUpStationId, setDownStationId, 10));

        upStationId = String.valueOf(setDownStationId);
        downStationId = String.valueOf(jdbcStationDao.save("잠실역"));
        lineId = String.valueOf(setLineId);
    }

    @DisplayName("구간을 등록한다.")
    @Test
    void createSection() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", "10");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteStation() {
        // given
        jdbcSectionDao
                .save(new Section(Long.parseLong(lineId), Long.parseLong(upStationId), Long.parseLong(downStationId),
                        10));
        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId + "/sections?stationId=" + upStationId)
                .then().log().all()
                .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
