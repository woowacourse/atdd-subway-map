package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.common.ErrorResponse;
import wooteco.subway.station.StationDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.execute("SET foreign_key_checks=0;");
        jdbcTemplate.execute("truncate table SECTION");
        jdbcTemplate.execute("alter table SECTION alter column ID restart with 1");
        jdbcTemplate.execute("truncate table STATION");
        jdbcTemplate.execute("alter table STATION alter column ID restart with 1");
        jdbcTemplate.execute("truncate table LINE");
        jdbcTemplate.execute("alter table LINE alter column ID restart with 1");
        jdbcTemplate.execute("insert into LINE (name, color) values ('9호선', '황토')");
        jdbcTemplate.execute("insert into LINE (name, color) values ('2호선', '초록')");
        jdbcTemplate.execute("SET foreign_key_checks=1;");
        stationDao.save("가양역");
        stationDao.save("증미역");
        stationDao.save("등촌역");
        stationDao.save("염창역");
        stationDao.save("신목동역");
        String sql = "insert into SECTION (LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE) values(?,?,?,?)";
        jdbcTemplate.update(sql, 1L, 1L, 2L, 10);
        jdbcTemplate.update(sql, 1L, 2L, 3L, 10);
        jdbcTemplate.update(sql, 2L, 2L, 3L, 10);
    }

    @Test
    @DisplayName("정상적인 중간 구간 저장")
    public void saveSectionWithNormalCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(2L, 4L, 1)
        );

        SectionResponse sectionResponse = response.body().as(SectionResponse.class);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(4L, 1L, 2L, 4L, 1));
    }

    @Test
    @DisplayName("등록되지 않은 노선에 구간 저장")
    public void saveSectionWithNonExistingLineCase() {
        ExtractableResponse<Response> response = RestAssured.given()
                .body(new SectionRequest(2L, 4L, 1))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/3/sections")
                .then()
                .extract();

        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);
        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION", "해당 노선은 등록되지 않은 노선입니다."));
    }

    @Test
    @DisplayName("역 사이의 거리가 기존 구간의 거리 이상일 경우의 중간 구간 저장")
    public void saveSectionWithDistanceExceptionCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(2L, 4L, 10)
        );

        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);
        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION",
                        "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다."));

        ExtractableResponse<Response> response2 = createSectionResponse(
                new SectionRequest(5L, 2L, 10)
        );

        ErrorResponse errorResponse2 = response2.body().as(ErrorResponse.class);
        assertThat(errorResponse2)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION",
                        "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다."));
    }

    @Test
    @DisplayName("상행 종점 구간 등록")
    public void saveSectionWithUpEndStationCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(4L, 1L, 1)
        );

        SectionResponse sectionResponse = response.body().as(SectionResponse.class);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(4L, 1L, 4L, 1L, 1));
    }

    @Test
    @DisplayName("행 종점 구간 등록")
    public void saveSectionWithDownEndPointCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(2L, 4L, 1)
        );

        SectionResponse sectionResponse = response.body().as(SectionResponse.class);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(4L, 1L, 2L, 4L, 1));
    }

    @Test
    @DisplayName("구간의 양 역이 노선에 모두 포함될 경우의 구간 등록")
    public void saveSectionWithBothStationContainCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(1L, 2L, 1)
        );

        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);
        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION",
                        "구간의 양 역이 노선에 둘 다 존재해서는 안되고, 둘 다 존재하지 않아서도 안됩니다."));
    }

    @Test
    @DisplayName("구간의 양 역이 노선에 아무것도 포함된 것이 없을 경우의 구간 등록")
    public void saveSectionWithNeitherStationContainCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(4L, 5L, 1)
        );

        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);
        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION",
                        "구간의 양 역이 노선에 둘 다 존재해서는 안되고, 둘 다 존재하지 않아서도 안됩니다."));
    }

    @Test
    @DisplayName("종점이 포함된 역의 구간 삭제")
    public void deleteSectionWithContainingEndStationCase() {
        deleteSectionResponse(1L);
        SectionResponse sectionResponse = findByUpStationId(2L);

        assertThatThrownBy(() -> findByUpStationId(1L))
                .isInstanceOf(EmptyResultDataAccessException.class);
        assertThat(sectionDao.numberOfEnrolledSection(1L)).isEqualTo(1);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(2L, 1L, 2L, 3L, 10));
    }

    @Test
    @DisplayName("중간 구간 삭제")
    public void deleteSectionWithMiddleCase() {
        ExtractableResponse<Response> response = deleteSectionResponse(2L);

        SectionResponse sectionResponse = findByUpStationId(1L);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(1L, 1L, 1L, 3L, 20));
    }

    @Test
    @DisplayName("등록된 구간이 1개 밖에 없는 경우 구간 삭제")
    public void deleteSectionWithContainingOnlyOneSectionCase() {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/2/sections?stationId=2")
                .then()
                .extract();
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION",
                        "구간이 1개인 경우에는 구간을 삭제할 수 없습니다."));
    }

    @Test
    @DisplayName("등록되지 않은 노선에 구간 삭제")
    public void deleteSectionWithNonExistingLineCase() {
        ExtractableResponse<Response> response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/3/sections?stationId=1")
                .then()
                .extract();
        ErrorResponse errorResponse = response.as(ErrorResponse.class);

        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION",
                        "해당 노선은 등록되지 않은 노선입니다."));
    }

    private ExtractableResponse<Response> createSectionResponse(SectionRequest sectionRequest) {
        return RestAssured.given()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then()
                .extract();
    }

    private ExtractableResponse<Response> deleteSectionResponse(Long stationId) {
        String uri = "/lines/1/sections?stationId=" + stationId;
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(uri)
                .then()
                .extract();
    }

    private SectionResponse findByUpStationId(Long upStationId) {
        return jdbcTemplate.queryForObject(
                "select id, line_id, up_station_id, down_station_id, distance from SECTION where line_id = 1 and up_station_id = ?",
                (rs, num) -> new SectionResponse(
                        rs.getLong("id"),
                        rs.getLong("line_id"),
                        upStationId,
                        rs.getLong("down_station_id"),
                        rs.getInt("distance")
                ), upStationId
        );
    }
}
