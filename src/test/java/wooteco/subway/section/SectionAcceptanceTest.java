package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.TestUtils;
import wooteco.subway.line.controller.dto.LineRequest;
import wooteco.subway.line.controller.dto.LineResponse;
import wooteco.subway.section.controller.dto.SectionRequest;
import wooteco.subway.station.controller.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능")
@Sql("classpath:stationInit.sql")
class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 구간 추가 성공 - 중간 구간")
    @Test
    void addSection_middle() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        final LineResponse addLineResponse = TestUtils.postLine(lineTwoRequest)
                .as(LineResponse.class);
        final Long lineId = addLineResponse.getId();

        // when
        final SectionRequest yangjaeSectionRequest = TestUtils.STATION_ONE_TO_THREE_SECTION_REQUEST;
        ExtractableResponse<Response> addSectionResponse = TestUtils.postSection(lineId, yangjaeSectionRequest);

        // then
        assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> getLineResponse = TestUtils.getLine(lineId);
        final LineResponse responseWithStations = getLineResponse.as(LineResponse.class);
        final List<String> stationNames = responseWithStations.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        final List<String> expectedStationNames = Arrays.asList(
                TestUtils.JAMSIL_STATION_REQUEST.getName(),
                TestUtils.YANGJAE_STATION_REQUEST.getName(),
                TestUtils.GANGNAM_STATION_REQUEST.getName()
        );
        assertThat(stationNames).containsAll(expectedStationNames);
    }

    @DisplayName("지하철 구간 추가 성공 - 최상단(upStation)")
    @Test
    void addSection_upStation() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        final LineResponse addLineResponse = TestUtils.postLine(lineTwoRequest)
                .as(LineResponse.class);
        final Long lineId = addLineResponse.getId();

        // when
        final SectionRequest yangjaeSectionRequest = TestUtils.STATION_THREE_TO_ONE_SECTION_REQUEST;
        ExtractableResponse<Response> addSectionResponse = TestUtils.postSection(lineId, yangjaeSectionRequest);

        // then
        assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> getLineResponse = TestUtils.getLine(lineId);
        final LineResponse responseWithStations = getLineResponse.as(LineResponse.class);
        final List<String> stationNames = responseWithStations.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        final List<String> expectedStationNames = Arrays.asList(
                TestUtils.YANGJAE_STATION_REQUEST.getName(),
                TestUtils.JAMSIL_STATION_REQUEST.getName(),
                TestUtils.GANGNAM_STATION_REQUEST.getName()
        );
        assertThat(stationNames).containsAll(expectedStationNames);
    }

    @DisplayName("지하철 구간 추가 성공 - 최하단(downStation)")
    @Test
    void addSection_downStation() {
        // given
        final LineRequest lineTwoRequest = TestUtils.LINE_TWO_REQUEST;
        final LineResponse addLineResponse = TestUtils.postLine(lineTwoRequest)
                .as(LineResponse.class);
        final Long lineId = addLineResponse.getId();

        // when
        final SectionRequest yangjaeSectionRequest = TestUtils.STATION_TWO_TO_THREE_SECTION_REQUEST;
        ExtractableResponse<Response> addSectionResponse = TestUtils.postSection(lineId, yangjaeSectionRequest);

        // then
        assertThat(addSectionResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        ExtractableResponse<Response> getLineResponse = TestUtils.getLine(lineId);
        final LineResponse responseWithStations = getLineResponse.as(LineResponse.class);
        final List<String> stationNames = responseWithStations.getStations()
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
        final List<String> expectedStationNames = Arrays.asList(
                TestUtils.JAMSIL_STATION_REQUEST.getName(),
                TestUtils.GANGNAM_STATION_REQUEST.getName(),
                TestUtils.YANGJAE_STATION_REQUEST.getName()
                );
        assertThat(stationNames).containsAll(expectedStationNames);
    }
}
