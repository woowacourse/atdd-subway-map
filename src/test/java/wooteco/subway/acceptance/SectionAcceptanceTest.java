package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    
    private static final long 강남역_ID = 1L;
    private static final long 역삼역_ID = 2L;
    private static final long 선릉역_ID = 3L;

    @BeforeEach
    void setUpData() {
        createStation("강남역");
        createStation("역삼역");
        createStation("선릉역");
        createStation("잠실역");
        createLine("2호선", "bg-red-600",  강남역_ID, 역삼역_ID, 10);
    }

    @Test
    @DisplayName("하행에 정상적으로 구간을 연결하는 경우를 테스트한다")
    void createSectionTest() {
        ExtractableResponse<Response> response = createSection(역삼역_ID, 선릉역_ID, 5);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("이미 등록된 상행과 하행을 연결하는 구간을 추가하는 경우 예외를 발생시킨다.")
    void createSectionDuplicateTest() {
        ExtractableResponse<Response> response = createSection(강남역_ID, 역삼역_ID, 5);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("존재하지 않는 상행과 하행을 연결하는 구간을 추가하는 경우 예외를 발생시킨다.")
    void createSectionNoExistTest() {
        ExtractableResponse<Response> response = createSection(강남역_ID, 역삼역_ID, 5);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("갈래길로 인해 하나의 길로 통합되는 경우를 테스트한다.")
    void createSectionForkTest() {
        ExtractableResponse<Response> response = createSection(강남역_ID, 선릉역_ID, 5);

        Section section = getSections().get(1);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(section.getUpStationId()).isEqualTo(선릉역_ID),
                () -> assertThat(section.getDownStationId()).isEqualTo(역삼역_ID),
                () -> assertThat(section.getDistance()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("단 2개의 역만 있는 경우는 구간 제거 시 예외를 발생시킨다.")
    void deleteSectionOnlyTwoStationTest() {
        ExtractableResponse<Response> response = deleteSection(강남역_ID);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("상행과 하행에 모두 걸쳐있는 역의 경우 제거 시 양옆의 구간을 통합시킨다")
    void deleteSectionOverlapTest() {
        createSection(역삼역_ID, 선릉역_ID, 5);
        ExtractableResponse<Response> response = deleteSection(역삼역_ID);
        Section section = getSections().get(0);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(section.getUpStationId()).isEqualTo(강남역_ID),
                () -> assertThat(section.getDownStationId()).isEqualTo(선릉역_ID)
        );
    }

    @Test
    @DisplayName("상행 종점이나 하행 종점인 경우 그냥 제거된다.")
    void deleteSectionFirstOrEndTest() {
        createSection(역삼역_ID, 선릉역_ID, 1);
        ExtractableResponse<Response> response = deleteSection(강남역_ID);
        Section section = getSections().get(0);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(getSections().size()).isEqualTo(1),
                () -> assertThat(section.getUpStationId()).isEqualTo(2),
                () -> assertThat(section.getDownStationId()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("존재하지 않는 역을 제거하는 경우 예외를 발생시킨다.")
    void deleteSectionNotExistTest() {

        ExtractableResponse<Response> response = deleteSection(9999L);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private List<Section> getSections() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/1")
                .then().log().all()
                .extract();

        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        for (StationResponse station : stations) {
            System.out.println(station.getId());
        }

        List<Section> sections = new ArrayList<>();

        for (int i = 1; i < stations.size(); i++) {
            sections.add(new Section(강남역_ID, stations.get(i - 1).getId(), stations.get(i).getId(), 5));
        }

        return sections;
    }

    private ExtractableResponse<Response> deleteSection(final Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/1/sections?stationId=" + stationId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createSection(final Long upStationId, final Long downStationId, final Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();
    }

    private void createLine(final String name, final String color, final Long upStationId,
                            final Long downStationId, final Integer distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private void createStation(final String name) {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
