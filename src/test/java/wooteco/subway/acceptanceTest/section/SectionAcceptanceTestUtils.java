package wooteco.subway.acceptanceTest.section;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.createLine;
import static wooteco.subway.acceptanceTest.station.StationAcceptanceTestUtils.createStationWithName;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.DEFAULT_SECTION_DISTANCE;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.LINE_COLOR;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.LINE_ID;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.LINE_NAME;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.NEW_STATION;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.response.line.LineStationsListResponseDto;
import wooteco.subway.controller.dto.response.station.StationResponseDto;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

public class SectionAcceptanceTestUtils {

    public static ExtractableResponse<Response> createLineWithSectionsOf(String lineName, String lineColor, List<Station> stations) {
        List<Station> sortedStationsById = getSortedStationsById(stations);
        createStations(sortedStationsById);
        createStationWithName(NEW_STATION.getName());
        ExtractableResponse<Response> response = createLine(lineName, lineColor, stations.get(0).getId(), stations.get(1).getId(), DEFAULT_SECTION_DISTANCE);
        createRemainedSections(stations);
        return response;
    }

    public static ExtractableResponse<Response> createLineWithSectionsOf(List<Station> stations) {
        return createLineWithSectionsOf(LINE_NAME, LINE_COLOR, stations);
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
            .post("/lines/{id}/sections", LINE_ID)
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
        LineStationsListResponseDto responseDto = getAllStationsListOf(LINE_ID);
        assertStationsListResponseDto(responseDto, stations);
    }

    private static LineStationsListResponseDto getAllStationsListOf(Long lineId) {
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
        return response.as(LineStationsListResponseDto.class);
    }

    private static void assertStationsListResponseDto(LineStationsListResponseDto responseDto, List<Station> stations) {
        assertDefaultLineInforms(responseDto);
        assertStationsListResponseDtosOrder(responseDto.getStations(), stations);
    }

    private static void assertDefaultLineInforms(LineStationsListResponseDto responseDto) {
        assertThat(responseDto.getId()).isEqualTo(LINE_ID);
        assertThat(responseDto.getName()).isEqualTo(LINE_NAME);
        assertThat(responseDto.getColor()).isEqualTo(LINE_COLOR);
    }

    private static void assertStationsListResponseDtosOrder(List<StationResponseDto> actualDtos, List<Station> expectedStations) {
        assertThat(actualDtos).hasSize(expectedStations.size());
        for (int i = 0; i < actualDtos.size(); i++) {
            StationResponseDto actualDto = actualDtos.get(i);
            Station expectedStation = expectedStations.get(i);
            assertThat(actualDto.getId()).isEqualTo(expectedStation.getId());
            assertThat(actualDto.getName()).isEqualTo(expectedStation.getName());
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
