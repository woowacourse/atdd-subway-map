package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.station.controller.dto.StationRequest;
import wooteco.subway.station.domain.Station;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@Transactional
public class SectionAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("구간 추가 - 종점")
    void addSectionEnd() {
        // given
        Station 강남역 = createStation(new StationRequest("강남역"));
        Station 역삼역 = createStation(new StationRequest("역삼역"));
        Station 잠실역 = createStation(new StationRequest("잠실역"));

        Long lineId = createLine(new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 5));

        LineRequest addLineRequest = new LineRequest("2호선", "red", 역삼역.getId(), 잠실역.getId(), 5);

        // when
        ExtractableResponse<Response> response = addSection(addLineRequest, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 추가 - 역 사이")
    void addSectionBetween() {
        // given
        Station 강남역 = createStation(new StationRequest("강남역"));
        Station 역삼역 = createStation(new StationRequest("역삼역"));
        Station 잠실역 = createStation(new StationRequest("잠실역"));

        Long lineId = createLine(new LineRequest("2호선", "red", 강남역.getId(), 잠실역.getId(), 5));

        LineRequest addLineRequest = new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 2);

        // when
        ExtractableResponse<Response> response = addSection(addLineRequest, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 추가 - 역 사이에 추가 시 기존 거리보다 크며 추가할 수 없다.")
    void addSectionDistanceException() {
        // given
        Station 강남역 = createStation(new StationRequest("강남역"));
        Station 역삼역 = createStation(new StationRequest("역삼역"));
        Station 잠실역 = createStation(new StationRequest("잠실역"));

        Long lineId = createLine(new LineRequest("2호선", "red", 강남역.getId(), 잠실역.getId(), 5));

        LineRequest addLineRequest = new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 10);

        // when
        ExtractableResponse<Response> response = addSection(addLineRequest, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("구간 추가 - 상행역, 하행역이 같을 경우 추가할 수 없다.")
    void addSectionSameException() {
        // given
        Station 강남역 = createStation(new StationRequest("강남역"));
        Station 역삼역 = createStation(new StationRequest("역삼역"));

        Long lineId = createLine(new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 5));

        LineRequest addLineRequest = new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 10);

        // when
        ExtractableResponse<Response> response = addSection(addLineRequest, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("구간 추가 - 해당 노선에 존재하지 않는 상행역, 하행역일 경우 추가할 수 없다.")
    void addSectionNoneException() {
        // given
        Station 강남역 = createStation(new StationRequest("강남역"));
        Station 역삼역 = createStation(new StationRequest("역삼역"));
        Station 잠실역 = createStation(new StationRequest("잠실역"));
        Station station4 = createStation(new StationRequest("교대역"));

        Long lineId = createLine(new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 5));

        LineRequest addLineRequest = new LineRequest("2호선", "red", 잠실역.getId(), station4.getId(), 10);

        // when
        ExtractableResponse<Response> response = addSection(addLineRequest, lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("구간 삭제 - 종점")
    void deleteSectionEnd() {
        // given
        Station 강남역 = createStation(new StationRequest("강남역"));
        Station 역삼역 = createStation(new StationRequest("역삼역"));
        Station 잠실역 = createStation(new StationRequest("잠실역"));

        Long lineId = createLine(new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 5));

        LineRequest addLineRequest = new LineRequest("2호선", "red", 역삼역.getId(), 잠실역.getId(), 10);

        addSection(addLineRequest, lineId);

        // when
        ExtractableResponse<Response> response = deleteSection(lineId, 잠실역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 삭제 - 역 사이")
    void deleteSectionBetween() {
        // given
        Station 강남역 = createStation(new StationRequest("강남역"));
        Station 역삼역 = createStation(new StationRequest("역삼역"));
        Station 잠실역 = createStation(new StationRequest("잠실역"));

        Long lineId = createLine(new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 5));

        LineRequest addLineRequest = new LineRequest("2호선", "red", 역삼역.getId(), 잠실역.getId(), 10);

        addSection(addLineRequest, lineId);

        // when
        ExtractableResponse<Response> response = deleteSection(lineId, 역삼역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 삭제 실패 - 구간이 하나일 경우")
    void deleteSectionFailOnlyOneSection() {
        // given
        Station 강남역 = createStation(new StationRequest("강남역"));
        Station 역삼역 = createStation(new StationRequest("역삼역"));

        Long lineId = createLine(new LineRequest("2호선", "red", 강남역.getId(), 역삼역.getId(), 5));

        // when
        ExtractableResponse<Response> response = deleteSection(lineId, 역삼역.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    private ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("lines/{id}/sections?stationId={sectionId}", lineId, stationId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> addSection(LineRequest lineRequest, Long lineId) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private Long createLine(LineRequest lineRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        return response.body().jsonPath().getLong("id");
    }

    private Station createStation(StationRequest stationRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        Long id = response.body().jsonPath().getLong("id");
        return new Station(id, stationRequest.getName());
    }
}
