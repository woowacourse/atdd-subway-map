package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@Sql("classpath:test.sql")
public class SectionAcceptanceTest extends AcceptanceTest {
    @Test
    @DisplayName("구간 추가")
    void createSection() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        SectionRequest req = new SectionRequest(1L, 2L, 5);

        // when
        postResponse("/stations", 강남역);
        postResponse("/stations", 잠실역);
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", req);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가 - 존재하지 않는 역으로 등록 요청이 들어오면 예외 발생")
    void createSectionWhenNotExistStations() {
        // given
        SectionRequest req = new SectionRequest(1L, 2L, 5);

        // when
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", req);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가 - 상행역과 하행역이 동일한 경우 예외를 발생한다.")
    void testSameStationsSection() {
        // given
        SectionRequest req = new SectionRequest(1L, 1L, 5);

        // when
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", req);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> postResponse(String path, StationRequest req) {
        return RestAssured.given().log().all()
                .body(req)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> postResponse(String path, LineRequest req) {
        return RestAssured.given().log().all()
                .body(req)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> postResponse(String path, SectionRequest req) {
        return RestAssured.given().log().all()
                .body(req)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(path)
                .then().log().all()
                .extract();
    }
}
