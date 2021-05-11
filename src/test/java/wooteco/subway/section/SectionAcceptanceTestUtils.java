package wooteco.subway.section;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.response.LineResponse;
import wooteco.subway.dto.station.response.StationResponse;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.fixture.DomainFixtures.*;
import static wooteco.subway.line.LineAcceptanceTestUtils.createLine;
import static wooteco.subway.station.StationAcceptanceTestUtils.createStationWithName;

public class SectionAcceptanceTestUtils {
    public static ExtractableResponse<Response> createLineWithSections(List<Station> stations) {
        return createLineWithSections(LINE_NAME, LINE_COLOR, stations);
    }

    public static ExtractableResponse<Response> createLineWithSections(String lineName, String lineColor, List<Station> stations) {
        List<Station> sortedStationsById = getSortedStationsById(stations);
        createStations(sortedStationsById);
        createStationWithName(NEW_STATION.getName());
        ExtractableResponse<Response> response = createLine(lineName, lineColor, stations.get(0).getId(), stations.get(1).getId(), DEFAULT_SECTION_DISTANCE);
        createRemainedSections(stations);
        return response;
    }

    private static List<Station> getSortedStationsById(List<Station> stations) {
        return stations.stream()
                .sorted(Comparator.comparingLong(Station::getId))
                .collect(Collectors.toList());
    }

    private static void createStations(List<Station> sortedStationsById) {
        for (Station station : sortedStationsById) {
            createStationWithName(station.getName());
        }
    }

    private static void createRemainedSections(List<Station> stations) {
        for (int i = 1; i + 1 < stations.size(); i++) {
            createSection(new Section(LINE_ID, stations.get(i).getId(), stations.get(i + 1).getId(), DEFAULT_SECTION_DISTANCE));
        }
    }

    public static ExtractableResponse<Response> createSection(Section newSection) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(newSection.getUpStationId()));
        params.put("downStationId", String.valueOf(newSection.getDownStationId()));
        params.put("distance", String.valueOf(newSection.getDistance()));

        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when()
                .post("/lines/{lineId}/sections", LINE_ID)
                .then().log().all()
                .extract();
    }

    public static void assertSectionCreatedResponseBody(Section newSection, ExtractableResponse<Response> response) {
        JsonPath responseJsonPath = response.jsonPath();
        assertThat(responseJsonPath.getLong("upStationId")).isEqualTo(newSection.getUpStationId());
        assertThat(responseJsonPath.getLong("downStationId")).isEqualTo(newSection.getDownStationId());
        assertThat(responseJsonPath.getInt("distance")).isEqualTo(newSection.getDistance());
    }

    public static void assertStationsList(List<Station> stations) {
        LineResponse response = getAllStationsListOf(LINE_ID);
        assertStationsListResponse(response, stations);
    }

    private static LineResponse getAllStationsListOf(Long lineId) {
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .get("/lines/{id}", lineId)
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        return response.as(LineResponse.class);
    }

    private static void assertStationsListResponse(LineResponse lineResponse, List<Station> stations) {
        assertDefaultLineInforms(lineResponse);
        assertStationsListResponsesOrder(lineResponse.getStations(), stations);
    }

    private static void assertDefaultLineInforms(LineResponse responseDto) {
        assertThat(responseDto.getId()).isEqualTo(LINE_ID);
        assertThat(responseDto.getName()).isEqualTo(LINE_NAME);
        assertThat(responseDto.getColor()).isEqualTo(LINE_COLOR);
    }

    private static void assertStationsListResponsesOrder(List<StationResponse> actualResponses, List<Station> expectedStations) {
        assertThat(actualResponses).hasSize(expectedStations.size());
        for (int i = 0; i < actualResponses.size(); i++) {
            StationResponse actual = actualResponses.get(i);
            Station expectedStation = expectedStations.get(i);
            assertThat(actual.getId()).isEqualTo(expectedStation.getId());
            assertThat(actual.getName()).isEqualTo(expectedStation.getName());
        }
    }

    public static ExtractableResponse<Response> deleteSection(Station stationToDelete) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/{lineId}/sections?stationId={stationId}", LINE_ID, stationToDelete.getId())
                .then().log().all()
                .extract();
    }
}
