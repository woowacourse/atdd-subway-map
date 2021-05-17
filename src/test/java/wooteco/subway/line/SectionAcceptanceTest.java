package wooteco.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.station.dto.StationResponse;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    private SectionRequest sectionRequest;
    private Long lineId;
    private Long firstUpStationId = 2L;
    private Long lastDownStationId = 3L;

    @BeforeEach
    void init() {
        LineRequest lineTwo = new LineRequest("2호선", "bg-red-600", firstUpStationId, lastDownStationId, 60);
        ExtractableResponse<Response> response = given()
                .log().all()
                .body(lineTwo)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        lineId = response.body().jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> postSections(SectionRequest request) {
        return given()
                .log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    @DisplayName("추가할 상행/하행 중 둘 다 중복되는 경우 예외처리한다.")
    @Test
    void invalidSections() {
        // given
        SectionRequest invalidSectionRequest = new SectionRequest(firstUpStationId, lastDownStationId, 10);

        // when
        ExtractableResponse<Response> response = postSections(invalidSectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("구간은 하나의 역만 중복될 수 있습니다.");
    }

    @DisplayName("추가할 상행/하행 중 모두 중복되지 않는 경우 예외처리한다.")
    @Test
    void invalidSections2() {
        // given
        Long newUpStationId = 1L;
        Long newDownStationId = 4L;
        SectionRequest invalidSectionRequest = new SectionRequest(newUpStationId, newDownStationId, 10);

        // when
        ExtractableResponse<Response> response = postSections(invalidSectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("구간은 하나의 역만 중복될 수 있습니다.");
    }

    @DisplayName("상행역을 기준으로 하행역을 추가한다.")
    @Test
    void addDownStationOfSection() {
        // given
        Long newStationId = 4L;
        sectionRequest = new SectionRequest(firstUpStationId, newStationId, 30);

        // when
        ExtractableResponse<Response> response = postSections(sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<StationResponse> stations = response.body().jsonPath().getList("stations", StationResponse.class);
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).getId()).isEqualTo(2L);
        assertThat(stations.get(1).getId()).isEqualTo(4L);
        assertThat(stations.get(2).getId()).isEqualTo(3L);
    }

    @DisplayName("하행역을 기준으로 상행역을 추가한다.")
    @Test
    void addUpStationOfSection() {
        // given
        Long newStationId = 4L;
        sectionRequest = new SectionRequest(newStationId, lastDownStationId, 30);

        // when
        ExtractableResponse<Response> response = postSections(sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<StationResponse> stations = response.body().jsonPath().getList("stations", StationResponse.class);
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).getId()).isEqualTo(2L);
        assertThat(stations.get(1).getId()).isEqualTo(4L);
        assertThat(stations.get(2).getId()).isEqualTo(3L);
    }

    @DisplayName("추가되는 구간의 거리 길이가 기존 구간보다 크거나 같은 경우 예외처리된다.")
    @ParameterizedTest
    @ValueSource(strings = {"30", "40"})
    void invalidSectionDistance(int distance) {
        // given
        sectionRequest = new SectionRequest(firstUpStationId, 4L, 30);
        postSections(sectionRequest);
        sectionRequest = new SectionRequest(4L, 1L, distance);

        // when
        ExtractableResponse<Response> response = postSections(sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("거리가 현재 존재하는 구간보다 크거나 같습니다!");
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        sectionRequest = new SectionRequest(firstUpStationId, 4L, 30);
        postSections(sectionRequest);
        Long deleteStationId = 3L;

        // when
        ExtractableResponse<Response> response = given().log().all()
                .when()
                .delete("lines/" + lineId + "/sections?stationId=" + deleteStationId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간 삭제 시 라인에 역이 2개인 경우 삭제 불가능하다.")
    @Test
    void invalidSectionWhenDelete() {
        // given
        Long deleteStationId = 3L;

        // when
        ExtractableResponse<Response> response = given().log().all()
                .when()
                .delete("lines/" + lineId + "/sections?stationId=" + deleteStationId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("종점역만 남은 경우 삭제를 수행할 수 없습니다!");
    }
}
