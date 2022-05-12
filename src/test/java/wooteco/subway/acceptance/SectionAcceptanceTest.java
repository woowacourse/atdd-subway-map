package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("상행 종점이 같은 구간을 추가한다.")
    @Test
    void createUpperMiddleSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station3.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        // when
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 5);
        ExtractableResponse<Response> response = createSection(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점이 같은 구간을 추가한다.")
    @Test
    void createLowerMiddleSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station3.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        // when
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 5);
        ExtractableResponse<Response> response = createSection(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("상행 종점을 연장한다.")
    @Test
    void createUpperSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station2.getId(), station3.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        // when
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response = createSection(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점을 연장한다.")
    @Test
    void createLowerSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        // when
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 10);
        ExtractableResponse<Response> response = createSection(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("길이가 기존 구간의 길이를 초과한 구간을 추가할 경우 BAD_REQUEST 를 반환한다.")
    @Test
    void createLongerMiddleSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station3.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        // when
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 10);
        ExtractableResponse<Response> response = createSection(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행과 하행 종점이 모두 포함되지 않는 구간을 추가할 경우 BAD_REQUEST 를 반환한다.")
    @Test
    void createNotMatchingSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");
        StationRequest stationRequest4 = new StationRequest("성수역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);
        Station station4 = createStation(stationRequest4).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station3.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        // when
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station4.getId(), 10);
        ExtractableResponse<Response> response = createSection(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행과 하행 종점이 모두 동일한 구간을 추가할 경우 BAD_REQUEST 를 반환한다.")
    @Test
    void createAllMatchingSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        // when
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response = createSection(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행 종점을 제거한다.")
    @Test
    void deleteUpperSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 10);
        createSection(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> response = deleteSection(station1, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("중간역을 제거한다.")
    @Test
    void deleteMiddleSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 10);
        createSection(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> response = deleteSection(station2, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점을 제거한다.")
    @Test
    void deleteLowerSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 10);
        createSection(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> response = deleteSection(station3, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간이 하나뿐인 노선의 구간을 삭제할 경우 BAD_REQUEST 를 반환한다.")
    @Test
    void deleteMinimumSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = deleteSection(station2, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 포함되지 않는 구간을 삭제할 경우 NOT_FOUND 를 반환한다.")
    @Test
    void deleteNotExistingSection() {
        // given
        StationRequest stationRequest1 = new StationRequest("강남역");
        StationRequest stationRequest2 = new StationRequest("역삼역");
        StationRequest stationRequest3 = new StationRequest("잠실역");
        StationRequest stationRequest4 = new StationRequest("성수역");

        Station station1 = createStation(stationRequest1).as(Station.class);
        Station station2 = createStation(stationRequest2).as(Station.class);
        Station station3 = createStation(stationRequest3).as(Station.class);
        Station station4 = createStation(stationRequest4).as(Station.class);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        long lineId = Long.parseLong(createLine(lineRequest).header("Location").split("/")[2]);

        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 10);
        createSection(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> response = deleteSection(station4, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> deleteSection(Station station1, long lineId) {
        return RestAssured.given().log().all()
                .queryParam("stationId", station1.getId())
                .when()
                .delete("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }
}
