package wooteco.subway.acceptanceTest.section;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.requestCreateLineAndGetResponse;
import static wooteco.subway.acceptanceTest.station.StationAcceptanceTestUtils.requestCreateStationWithNameAndGetResponse;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.controller.dto.response.line.LineWithAllStationsInOrderResponseDto;
import wooteco.subway.controller.dto.response.station.StationResponseDto;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SectionAcceptanceTestUtils {
    public static final Station STATION_1 = new Station(4L, "첫 번째 역");
    public static final Station STATION_2 = new Station(1L, "두 번째 역");
    public static final Station STATION_3 = new Station(3L, "세 번째 역");
    public static final Station STATION_4 = new Station(2L, "네 번째 역");
    public static final Long LINE_ID = 1L;
    public static final String LINE_NAME = "노선1";
    public static final String LINE_COLOR = "노선1의 색깔";
    public static final Long NEW_STATION_ID = 5L;
    public static final String NEW_STATION_NAME = "새로운 역";
    public static final int NEW_SECTION_DISTANCE = 14;
    public static final int DEFAULT_SECTION_DISTANCE = 20;

    public static ExtractableResponse<Response> requestCreateAndSetLineWithSectionsAndGetResponse(List<Station> stations) {
        for (Station station : stations) {
            requestCreateStationWithNameAndGetResponse(station.getName());
        }

        ExtractableResponse<Response> response = requestCreateLineAndGetResponse(LINE_NAME, LINE_COLOR,
            stations.get(0).getId(), stations.get(1).getId(), DEFAULT_SECTION_DISTANCE);

        for (int i = 1; i + 1 < stations.size(); i++) {
            requestCreateSectionAndGetResponse(new Section(stations.get(i), stations.get(i + 1), DEFAULT_SECTION_DISTANCE));
        }
        return response;
    }

    public static ExtractableResponse<Response> requestCreateSectionAndGetResponse(Section newSection) {
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

    public static void requestAndAssertLineWithAllStationsInOrderResponseDtos(Stream<Station> expectedStations) {

        LineWithAllStationsInOrderResponseDto responseDto = requestAllStationsOfLineInOrderAndGetResponseDto(LINE_ID);

        List<StationResponseDto> expectedStationResponseDtos = expectedStations
            .map(StationResponseDto::new)
            .collect(Collectors.toList());

        assertLineWithAllStationsInOrderResponseDto(responseDto, expectedStationResponseDtos);
    }

    public static LineWithAllStationsInOrderResponseDto requestAllStationsOfLineInOrderAndGetResponseDto(Long lineId) {
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

        return response.jsonPath().getObject(".", LineWithAllStationsInOrderResponseDto.class);
    }

    public static void assertLineWithAllStationsInOrderResponseDto(
        LineWithAllStationsInOrderResponseDto responseDto, List<StationResponseDto> expectedStationResponseDtos) {

        assertDefaultLineInforms(responseDto);
        assertStationResponseDtosWithOrder(responseDto.getStations(), expectedStationResponseDtos);
    }

    private static void assertDefaultLineInforms(LineWithAllStationsInOrderResponseDto responseDto) {
        assertThat(responseDto.getId()).isEqualTo(LINE_ID);
        assertThat(responseDto.getName()).isEqualTo(LINE_NAME);
        assertThat(responseDto.getColor()).isEqualTo(LINE_COLOR);
    }

    public static void assertStationResponseDtosWithOrder(List<StationResponseDto> actualDtos, List<StationResponseDto> expectedDtos) {
        for (int i = 0; i < actualDtos.size(); i++) {
            StationResponseDto actualDto = actualDtos.get(i);
            StationResponseDto expectedDto = expectedDtos.get(i);

            assertThat(actualDto.getId()).isEqualTo(expectedDto.getId());
            assertThat(actualDto.getName()).isEqualTo(expectedDto.getName());
        }
    }

    public static ExtractableResponse<Response> requestDeleteSectionAndGetResponse(Station stationToDelete) {
        return RestAssured.given().log().all()
            .when()
            .delete("/lines/{lineId}/sections?stationId={stationId}", LINE_ID, stationToDelete.getId())
            .then().log().all()
            .extract();
    }
}
