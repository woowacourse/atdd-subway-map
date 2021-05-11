package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.common.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        jdbcTemplate.execute("truncate table SECTION");
        jdbcTemplate.execute("alter table SECTION alter column ID restart with 1");
        String sql = "insert into SECTION (LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE) values(?,?,?,?)";
        jdbcTemplate.update(sql, 1L, 1L, 2L, 10);
        jdbcTemplate.update(sql, 1L, 2L, 3L, 10);
    }

    @Test
    @DisplayName("정상적인 중간 구간 저장")
    public void saveSectionWithNormalCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(2L, 10L, 1)
        );

        SectionResponse sectionResponse = response.body().as(SectionResponse.class);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(3L, 1L, 2L, 10L, 1));
    }

    @Test
    @DisplayName("역 사이의 거리가 기존 구간의 거리 이상일 경우의 중간 구간 저장")
    public void saveSectionWithDistanceExceptionCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(2L, 10L, 10)
        );

        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);
        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION",
                        "역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다."));
    }

    @Test
    @DisplayName("상행 종점 구간 등록")
    public void saveSectionWithUpEndStationCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(10L, 1L, 1)
        );

        SectionResponse sectionResponse = response.body().as(SectionResponse.class);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(3L, 1L, 10L, 1L, 1));
    }

    @Test
    @DisplayName("행 종점 구간 등록")
    public void saveSectionWithDownEndPointCase() {
        ExtractableResponse<Response> response = createSectionResponse(
                new SectionRequest(2L, 10L, 1)
        );

        SectionResponse sectionResponse = response.body().as(SectionResponse.class);
        assertThat(sectionResponse)
                .usingRecursiveComparison()
                .isEqualTo(new SectionResponse(3L, 1L, 2L, 10L, 1));
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
                new SectionRequest(10L, 20L, 1)
        );

        ErrorResponse errorResponse = response.body().as(ErrorResponse.class);
        assertThat(errorResponse)
                .usingRecursiveComparison()
                .ignoringFields("timeStamp")
                .isEqualTo(new ErrorResponse("SECTION_EXCEPTION",
                        "구간의 양 역이 노선에 둘 다 존재해서는 안되고, 둘 다 존재하지 않아서도 안됩니다."));
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
}
