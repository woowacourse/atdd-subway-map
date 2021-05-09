package wooteco.subway.acceptanceTest.section;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.DEFAULT_SECTION_DISTANCE;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.LINE_ID;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.NEW_SECTION_DISTANCE;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.NEW_STATION;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.NEW_STATION_ID;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.NEW_STATION_NAME;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.STATION_1;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.STATION_2;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.STATION_3;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.STATION_4;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.assertSectionCreatedResponseBody;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.assertStationsList;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.createLineWithSectionsOf;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.createSection;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.deleteSection;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.acceptanceTest.AcceptanceTest;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 구간 추가 - 상행 종점역이 새로운 구간의 하행역일 때")
    @Test
    void addSection_When_FirstStation_Is_DownStationOfNewSection() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Section newSection = new Section(LINE_ID, NEW_STATION, STATION_1, NEW_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        List<Station> afterStations = Arrays.asList(NEW_STATION, STATION_1, STATION_2, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("지하철 구간 추가 - 하행 종점역이 새로운 구간의 상행역일 때")
    @Test
    void addSection_When_LastStation_Is_UpStationOfNewSection() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Section newSection = new Section(LINE_ID, STATION_4, NEW_STATION, NEW_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4, NEW_STATION);
        assertStationsList(afterStations);
    }

    @DisplayName("지하철 구간 추가 - 상행 종점역이 새로운 구간의 상행역일 때")
    @Test
    void addSection_When_FirstStation_Is_UpStationOfNewSection() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Section newSection = new Section(LINE_ID, STATION_1, NEW_STATION, NEW_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        List<Station> afterStations = Arrays.asList(STATION_1, NEW_STATION, STATION_2, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("지하철 구간 추가 - 하행 종점역이 새로운 구간의 하행역일 때")
    @Test
    void addSection_When_DownSideFinalStation_Is_DownStationOfNewSection() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Section newSection = new Section(LINE_ID, NEW_STATION, STATION_4, NEW_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2, STATION_3, NEW_STATION, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("지하철 구간 추가 - 2번째 역이 새로운 구간의 하행역일 때")
    @Test
    void addSection_When_Station2_Is_DownStationOfNewSection() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Section newSection = new Section(LINE_ID, NEW_STATION, STATION_2, NEW_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        List<Station> afterStations = Arrays.asList(STATION_1, NEW_STATION, STATION_2, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("지하철 구간 추가 - 2번째 역이 새로운 구간의 상행역일 때")
    @Test
    void addSection_When_Station2_Is_UpStationOfNewSection() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Section newSection = new Section(LINE_ID, STATION_2, NEW_STATION, NEW_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2, NEW_STATION, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("지하철 구간 추가 예외 - 추가할 구간의 상행역과 하행역이 기존 노선에 모두 없을 때")
    @Test
    void addSectionException_When_AllStationsOfSectionToAdd_NotExist() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Station newStation1 = new Station(NEW_STATION_ID, NEW_STATION_NAME);
        Station newStation2 = new Station(NEW_STATION_ID + 1, NEW_STATION_NAME + 1);
        Section newSection = new Section(LINE_ID, newStation1, newStation2, NEW_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("지하철 구간 추가 예외 - 추가할 구간의 상행역과 하행역이 기존 노선에 모두 존재할 때")
    @Test
    void addSectionException_When_AllStationsOfSectionToAdd_Exist() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Section newSection = new Section(LINE_ID, STATION_1, STATION_2, NEW_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("지하철 구간 추가 예외 - 추가할 구간의 길이가 추가될 기존 구간의 길이보다 크거나 같을 때")
    @Test
    void addSectionException_When_DistanceOfSectionToAdd_GreaterThanLineSection() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        Section newSection = new Section(LINE_ID, NEW_STATION, STATION_2, DEFAULT_SECTION_DISTANCE);
        ExtractableResponse<Response> response = createSection(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("노선 조회 - 상행 종점역부터 하행 종점역까지 연결된 순서대로 응답")
    @Test
    void getAllStationsOfLineInOrder() {
        // given
        List<Station> sectionsToCreate = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(sectionsToCreate);

        // when, then
        List<Station> expectedSections = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        assertStationsList(expectedSections);
    }

    @DisplayName("구간(역) 제거 - 상행 종점역을 제거할 때")
    @Test
    void deleteFirstStation() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        ExtractableResponse<Response> response = deleteSection(STATION_1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<Station> afterStations = Arrays.asList(STATION_2, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("구간(역) 제거 - 하행 종점역을 제거할 때")
    @Test
    void deleteLastStation() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        ExtractableResponse<Response> response = deleteSection(STATION_4);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2, STATION_3);
        assertStationsList(afterStations);
    }

    @DisplayName("구간(역) 제거 - 두 번째 역을 제거할 때")
    @Test
    void deleteSecondStation() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);
        createLineWithSectionsOf(beforeStations);

        // when
        ExtractableResponse<Response> response = deleteSection(STATION_2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_3, STATION_4);
        assertStationsList(afterStations);
    }

    @DisplayName("구간(역) 제거 예외 - 노선에 한 개의 구간만 존재할 때")
    @Test
    void deleteException_When_OnlyOneSectionExists() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2);
        createLineWithSectionsOf(beforeStations);

        // when
        ExtractableResponse<Response> response = deleteSection(STATION_1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2);
        assertStationsList(afterStations);
    }

    @DisplayName("구간(역) 제거 예외 - 노선에 삭제할 역을 갖고있는 구간이 존재하지 않을 때")
    @Test
    void deleteException_When_StationToDeleteNotExists() {
        // given
        List<Station> beforeStations = Arrays.asList(STATION_1, STATION_2, STATION_3);
        createLineWithSectionsOf(beforeStations);

        // when
        ExtractableResponse<Response> response = deleteSection(STATION_4);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Station> afterStations = Arrays.asList(STATION_1, STATION_2, STATION_3);
        assertStationsList(afterStations);
    }
}
