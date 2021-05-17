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
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationResponse;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철구간 관련 기능")
@Sql("/data.sql")
public class SectionAcceptanceTest extends AcceptanceTest {

    private final SectionRequest sectionRequest = new SectionRequest(2L, 3L, 10);

    @DisplayName("지하철구간을 생성한다.")
    @Test
    void createSection() {
        ExtractableResponse<Response> response = sectionPostRequest(sectionRequest, "/lines/1/sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        findLineForCreateSection();
    }

    private void findLineForCreateSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/1")
            .then().log().all()
            .extract();

        LineResponse lineResponse = new LineResponse(new Line(1L, "2호선", "초록색"),
            Arrays.asList(
                new Station(1L, "강남역"), new Station(2L, "역삼역"), new Station(3L, "아차산역")
            ));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(resultResponse).usingRecursiveComparison()
            .isEqualTo(lineResponse);
    }

    @DisplayName("노선과 이어지지 않는 구간 생성시 예외를 발생한다.")
    @Test
    void createDisconnectedSection() {
        SectionRequest disconnectedSectionRequest = new SectionRequest(4L, 5L, 10);
        ExtractableResponse<Response> response = sectionPostRequest(disconnectedSectionRequest, "/lines/1/sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 이미 존재하는 구간 생성시 예외를 발생한다.")
    @Test
    void createExistingSection() {
        SectionRequest existingSectionRequest = new SectionRequest(1L, 2L, 10);
        ExtractableResponse<Response> response = sectionPostRequest(existingSectionRequest, "/lines/1/sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 초과 길이 구간 생성시 예외를 발생한다.")
    @Test
    void createInvalidDistanceSection() {
        SectionRequest invalidDistanceSectionRequest = new SectionRequest(5L, 3L, 12);
        ExtractableResponse<Response> response = sectionPostRequest(invalidDistanceSectionRequest, "/lines/2/sections");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철구간을 삭제한다.")
    @Test
    void deleteSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .queryParam("stationId", 5)
            .delete("/lines/2/sections")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        findLineForDeleteSection();
    }

    private void findLineForDeleteSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/2")
            .then().log().all()
            .extract();

        LineResponse lineResponse = new LineResponse(new Line(2L, "경의중앙선", "하늘색"),
            Arrays.asList(new Station(4L, "탄현역"), new Station(6L, "홍대입구역")));

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.jsonPath().getObject(".", LineResponse.class);

        assertThat(resultResponse).usingRecursiveComparison()
            .isEqualTo(lineResponse);
    }

    @DisplayName("마지막 지하철구간을 삭제시 예외를 발생한다.")
    @Test
    void deleteLastSection() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .queryParam("stationId", 2)
            .delete("/lines/1/sections")
            .then().log().all()
            .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> sectionPostRequest(SectionRequest sectionRequest, String s) {
        return RestAssured.given().log().all()
            .body(sectionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(s)
            .then().log().all()
            .extract();
    }
}
