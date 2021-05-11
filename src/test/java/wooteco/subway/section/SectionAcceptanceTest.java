package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void beforeEach() {
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");

        postResponse("/stations", 강남역);
        postResponse("/stations", 잠실역);

        LineRequest 이호선 = new LineRequest("이호선", "green", 1L, 2L, 5);
        postResponse("/lines", 이호선);
    }

    @Test
    @DisplayName("구간 추가 - 추가하려는 구간의 상행이 등록된 구간의 하행종점인 경우")
    void createSection() {
        // given
        StationRequest 당산역 = new StationRequest("당산역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");

        SectionRequest 잠실에서당산 = new SectionRequest(2L, 3L, 5);

        // when
        postResponse("/stations", 당산역);
        postResponse("/stations", 왕십리역);
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", 잠실에서당산);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가 - 추가하려는 구간의 하행이 등록된 구간의 상행종점인 경우")
    void createSection2() {
        // given
        StationRequest 당산역 = new StationRequest("당산역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");

        SectionRequest 당산에서강남 = new SectionRequest(3L, 1L, 5);

        // when
        postResponse("/stations", 당산역);
        postResponse("/stations", 왕십리역);
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", 당산에서강남);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가 - 이미 등록된 노선인 경우 예외를 던진다.")
    void createSectionWhenAlreadyRegistered() {
        // given
        SectionRequest req = new SectionRequest(1L, 2L, 5);

        // when
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", req);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가 - 구간에 등록되어 있지 않은 역을 추가하는 경우 예외를 던진다.")
    void createSectionWhen() {
        // given
        StationRequest 당산역 = new StationRequest("당산역"); // id = 3
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4
        StationRequest 신림역 = new StationRequest("신림역"); // id = 5
        SectionRequest 잠실에서당산 = new SectionRequest(2L, 3L, 5);
        SectionRequest 왕십리에서신림 = new SectionRequest(4L, 5L, 2);

        // when
        postResponse("/stations", 당산역);
        postResponse("/stations", 왕십리역);
        postResponse("/stations", 신림역);
        postResponse("/lines/1/sections", 잠실에서당산);

        ExtractableResponse<Response> response = postResponse("/lines/1/sections", 왕십리에서신림);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가 - 존재하지 않는 역으로 등록 요청이 들어오면 예외 발생")
    void createSectionWhenNotExistStations() {
        // given
        SectionRequest req = new SectionRequest(3L, 4L, 5);

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

    @Test
    @DisplayName("구간 추가(중간) - 추가하려는 구간의 하행이 등록된 구간 리스트의 하행에 있는 경우")
    void createSectionAppendBeforeDown() {
        // given
        StationRequest 당산역 = new StationRequest("당산역"); // id = 3
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4

        SectionRequest 잠실에서왕십리 = new SectionRequest(2L, 4L, 5);
        SectionRequest 당산에서왕십리 = new SectionRequest(3L, 4L, 3);

        // when
        postResponse("/stations", 당산역);
        postResponse("/stations", 왕십리역);
        postResponse("/lines/1/sections", 잠실에서왕십리);
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", 당산에서왕십리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가(중간) - 추가하려는 구간의 거리가 등록된 구간 리스트의 거리 이상인 경우")
    void createWhenNewSectionsDistanceLonger() {
        // given
        StationRequest 당산역 = new StationRequest("당산역"); // id = 3
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4

        SectionRequest 잠실에서왕십리 = new SectionRequest(2L, 4L, 5);
        SectionRequest 당산에서왕십리 = new SectionRequest(3L, 4L, 6);

        // when
        postResponse("/stations", 당산역);
        postResponse("/stations", 왕십리역);
        postResponse("/lines/1/sections", 잠실에서왕십리);
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", 당산에서왕십리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가(중간) - 추가하려는 구간의 상행이 등록된 구간 리스트의 상행에 있는 경우")
    void createSectionAppendUp() {
        // given
        StationRequest 당산역 = new StationRequest("당산역"); // id = 3
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4

        SectionRequest 잠실에서왕십리 = new SectionRequest(2L, 4L, 5);
        SectionRequest 당산에서왕십리 = new SectionRequest(2L, 3L, 3);

        // when
        postResponse("/stations", 당산역);
        postResponse("/stations", 왕십리역);
        postResponse("/lines/1/sections", 잠실에서왕십리);
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", 당산에서왕십리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가(중간) - 추가하려는 구간의 거리가 등록된 구간 리스트의 거리 이상인 경우")
    void createWhenNewSectionsDistanceLonger2() {
        // given
        StationRequest 당산역 = new StationRequest("당산역"); // id = 3
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4

        SectionRequest 잠실에서왕십리 = new SectionRequest(2L, 4L, 5);
        SectionRequest 당산에서왕십리 = new SectionRequest(2L, 3L, 5);

        // when
        postResponse("/stations", 당산역);
        postResponse("/stations", 왕십리역);
        postResponse("/lines/1/sections", 잠실에서왕십리);
        ExtractableResponse<Response> response = postResponse("/lines/1/sections", 당산에서왕십리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        // given
        StationRequest 당산역 = new StationRequest("당산역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");

        SectionRequest 잠실에서당산 = new SectionRequest(2L, 3L, 5);

        // when
        postResponse("/stations", 당산역);
        postResponse("/stations", 왕십리역);
        postResponse("/lines/1/sections", 잠실에서당산);
        ExtractableResponse<Response> response = deleteResponse("/lines/1/sections?stationId=2");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

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

    private ExtractableResponse<Response> deleteResponse(String path) {
        return RestAssured.given().log().all()
                .when()
                .delete(path)
                .then().log().all()
                .extract();
    }
}
