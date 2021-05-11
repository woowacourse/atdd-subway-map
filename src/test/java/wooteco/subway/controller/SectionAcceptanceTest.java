package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.controller.dto.response.StationResponse;
import wooteco.subway.domain.station.Station;

public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간 추가 - 상행 종점 추가")
    @Test
    void addSection_top() {
        // given
        Station station1 = createTestStation("잠실역");
        Station station2 = createTestStation("잠실새내역");
        Station station3 = createTestStation("종합운동장역");

        Long lineId = createTestLine("신분당선", station1.getId(), station2.getId(), 10);

        // when
        ExtractableResponse<Response> addSectionResponse = addTestSection(lineId, station3.getId(), station1.getId(), 3);

        // then
        assertThat(addSectionResponse.body().jsonPath().getList("stations", StationResponse.class))
            .extracting("id", "name")
            .containsExactlyInAnyOrder(
                Tuple.tuple(station3.getId(), station3.getName()),
                Tuple.tuple(station1.getId(), station1.getName()),
                Tuple.tuple(station2.getId(), station2.getName())
            );
    }

    @DisplayName("구간 추가 - 하행 종점 추가")
    @Test
    void addSection_bottom() {
        // given
        Station station1 = createTestStation("잠실역");
        Station station2 = createTestStation("잠실새내역");
        Station station3 = createTestStation("종합운동장역");

        Long lineId = createTestLine("신분당선", station1.getId(), station2.getId(), 10);

        // when
        ExtractableResponse<Response> addSectionResponse = addTestSection(lineId, station2.getId(), station3.getId(), 3);

        // then
        assertThat(addSectionResponse.body().jsonPath().getList("stations", StationResponse.class))
            .extracting("id", "name")
            .containsExactlyInAnyOrder(
                Tuple.tuple(station1.getId(), station1.getName()),
                Tuple.tuple(station2.getId(), station2.getName()),
                Tuple.tuple(station3.getId(), station3.getName())
            );
    }

    @DisplayName("구간 추가 - 기존 구간 중간에 추가")
    @Test
    void addSection_middle() {
        // given
        Station station1 = createTestStation("잠실역");
        Station station2 = createTestStation("잠실새내역");
        Station station3 = createTestStation("종합운동장역");

        Long lineId = createTestLine("신분당선", station1.getId(), station2.getId(), 10);

        // when
        ExtractableResponse<Response> addSectionResponse = addTestSection(lineId, station1.getId(), station3.getId(), 3);

        // then
        assertThat(addSectionResponse.body().jsonPath().getList("stations", StationResponse.class))
            .extracting("id", "name")
            .containsExactlyInAnyOrder(
                Tuple.tuple(station1.getId(), station1.getName()),
                Tuple.tuple(station3.getId(), station3.getName()),
                Tuple.tuple(station2.getId(), station2.getName())
            );
    }

    @DisplayName("구간 추가 예외 - 역 사이에 새로운 역을 등록할 경우 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void addSection_exception_distance() {
        // given
        Station station1 = createTestStation("잠실역");
        Station station2 = createTestStation("잠실새내역");
        Station station3 = createTestStation("종합운동장역");

        Long lineId = createTestLine("신분당선", station1.getId(), station2.getId(), 3);

        // when
        ExtractableResponse<Response> addSectionResponse = addTestSection(lineId, station1.getId(), station3.getId(), 4);

        // then
        assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 추가 예외 - 상행역과 하행역이 모두 이미 해당 노선에 등록되어 있는 경우 추가할 수 없다.")
    @Test
    void addSection_exception_same_station() {
        // given
        Station station1 = createTestStation("잠실역");
        Station station2 = createTestStation("잠실새내역");

        Long lineId = createTestLine("신분당선", station1.getId(), station2.getId(), 10);

        // when
        ExtractableResponse<Response> addSectionResponse = addTestSection(lineId, station1.getId(), station2.getId(), 3);

        // then
        assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 추가 예외 - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void addSection_exception_none() {
        // given
        Station station1 = createTestStation("잠실역");
        Station station2 = createTestStation("잠실새내역");

        Long lineId = createTestLine("신분당선", station1.getId(), station2.getId(), 2);

        // when
        ExtractableResponse<Response> addSectionResponse = addTestSection(lineId, -1L, -1L, 3);

        // then
        assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private Station createTestStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all().extract();
        Long id = response.body().jsonPath().getLong("id");
        return new Station(id, name);
    }

    private Long createTestLine(String name, Long upStationId, Long downStationId, int distance) {
        Map<String, String> createLineParam = new HashMap<>();
        createLineParam.put("color", "bg-red-600");
        createLineParam.put("name", name);
        createLineParam.put("upStationId", String.valueOf(upStationId));
        createLineParam.put("downStationId", String.valueOf(downStationId));
        createLineParam.put("distance", String.valueOf(distance));
        ExtractableResponse<Response> createLineResponse = RestAssured.given().log().all()
            .body(createLineParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
        return createLineResponse.jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> addTestSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> addSectionParam = new HashMap<>();
        addSectionParam.put("upStationId", String.valueOf(upStationId));
        addSectionParam.put("downStationId", String.valueOf(downStationId));
        addSectionParam.put("distance", String.valueOf(distance));
        return RestAssured.given().log().all()
            .body(addSectionParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }
}
