package wooteco.subway.acceptanceTest.section;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.DEFAULT_SECTION_DISTANCE;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.NEW_SECTION_DISTANCE;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.NEW_STATION_ID;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.NEW_STATION_NAME;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.STATION_1;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.STATION_2;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.STATION_3;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.STATION_4;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.assertSectionCreatedResponseBody;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.requestAndAssertLineWithAllStationsInOrderResponseDtos;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.requestCreateAndSetLineWithSectionsAndGetResponse;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.requestCreateSectionAndGetResponse;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.requestDeleteSectionAndGetResponse;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.acceptanceTest.AcceptanceTest;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;

public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 구간 추가 - 상행 종점역이 새로운 구간의 하행역일 때")
    @Test
    void addSection_When_FirstStation_Is_DownStationOfNewSection() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station newStation = new Station(NEW_STATION_ID, NEW_STATION_NAME);
        Section newSection = new Section(newStation, STATION_1, NEW_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        Stream<Station> expectedStations = Stream.of(newStation, STATION_1, STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("지하철 구간 추가 - 하행 종점역이 새로운 구간의 상행역일 때")
    @Test
    void addSection_When_LastStation_Is_UpStationOfNewSection() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station newStation = new Station(NEW_STATION_ID, NEW_STATION_NAME);
        Section newSection = new Section(STATION_4, newStation, NEW_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_2, STATION_3, STATION_4, newStation);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("지하철 구간 추가 - 상행 종점역이 새로운 구간의 상행역일 때")
    @Test
    void addSection_When_FirstStation_Is_UpStationOfNewSection() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station newStation = new Station(NEW_STATION_ID, NEW_STATION_NAME);
        Section newSection = new Section(STATION_1, newStation, NEW_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        Stream<Station> expectedStations = Stream.of(STATION_1, newStation, STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("지하철 구간 추가 - 하행 종점역이 새로운 구간의 하행역일 때")
    @Test
    void addSection_When_DownSideFinalStation_Is_DownStationOfNewSection() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station newStation = new Station(NEW_STATION_ID, NEW_STATION_NAME);
        Section newSection = new Section(newStation, STATION_4, NEW_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_2, STATION_3, newStation, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("지하철 구간 추가 - 2번째 역이 새로운 구간의 하행역일 때")
    @Test
    void addSection_When_Station2_Is_DownStationOfNewSection() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station newStation = new Station(NEW_STATION_ID, NEW_STATION_NAME);
        Section newSection = new Section(newStation, STATION_2, NEW_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        Stream<Station> expectedStations = Stream.of(STATION_1, newStation, STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("지하철 구간 추가 - 2번째 역이 새로운 구간의 상행역일 때")
    @Test
    void addSection_When_Station2_Is_UpStationOfNewSection() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station newStation = new Station(NEW_STATION_ID, NEW_STATION_NAME);
        Section newSection = new Section(STATION_2, newStation, NEW_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertSectionCreatedResponseBody(newSection, response);

        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_2, newStation, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("지하철 구간 추가 예외 - 추가할 구간의 상행역과 하행역이 기존 노선에 모두 없을 때")
    @Test
    void addSectionException_When_AllStationsOfSectionToAdd_NotExist() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station newStation1 = new Station(NEW_STATION_ID, NEW_STATION_NAME);
        Station newStation2 = new Station(NEW_STATION_ID + 2, NEW_STATION_NAME + 2);
        Section newSection = new Section(newStation1, newStation2, NEW_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("지하철 구간 추가 예외 - 추가할 구간의 상행역과 하행역이 기존 노선에 모두 존재할 때")
    @Test
    void addSectionException_When_AllStationsOfSectionToAdd_Exist() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Section newSection = new Section(STATION_1, STATION_2, NEW_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("지하철 구간 추가 예외 - 추가할 구간의 길이가 추가될 기존 구간의 길이보다 크거나 같을 때")
    @Test
    void addSectionException_When_DistanceOfSectionToAdd_GreaterThanLineSection() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Section newSection = new Section(STATION_1, STATION_2, DEFAULT_SECTION_DISTANCE);

        ExtractableResponse<Response> response = requestCreateSectionAndGetResponse(newSection);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("노선 조회 - 상행 종점역부터 하행 종점역까지 연결된 순서대로 응답")
    @Test
    void getAllStationsOfLineInOrder() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when, then
        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("구간(역) 제거 - 상행 종점역을 제거할 때")
    @Test
    void deleteFirstStation() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station stationToDelete = STATION_1;
        ExtractableResponse<Response> response = requestDeleteSectionAndGetResponse(stationToDelete);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        Stream<Station> expectedStations = Stream.of(STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("구간(역) 제거 - 하행 종점역을 제거할 때")
    @Test
    void deleteLastStation() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station stationToDelete = STATION_4;
        ExtractableResponse<Response> response = requestDeleteSectionAndGetResponse(stationToDelete);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_2, STATION_3);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("구간(역) 제거 - 두 번째 역을 제거할 때")
    @Test
    void deleteSecondStation() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_1, STATION_2, STATION_3, STATION_4
        ));

        // when
        Station stationToDelete = STATION_2;
        ExtractableResponse<Response> response = requestDeleteSectionAndGetResponse(stationToDelete);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        Stream<Station> expectedStations = Stream.of(STATION_1, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("구간(역) 제거 예외 - 노선에 한 개의 구간만 존재할 때")
    @Test
    void deleteException_When_OnlyOneSectionExists() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Collections.singletonList(
            STATION_1
        ));

        // when
        Station stationToDelete = STATION_1;
        ExtractableResponse<Response> response = requestDeleteSectionAndGetResponse(stationToDelete);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        Stream<Station> expectedStations = Stream.of(STATION_1);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }

    @DisplayName("구간(역) 제거 예외 - 노선에 삭제할 역을 갖고있는 구간이 존재하지 않을 때")
    @Test
    void deleteException_When_StationToDeleteNotExists() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(
            STATION_2, STATION_3, STATION_4
        ));

        // when
        Station stationToDelete = STATION_1;
        ExtractableResponse<Response> response = requestDeleteSectionAndGetResponse(stationToDelete);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        Stream<Station> expectedStations = Stream.of(STATION_2, STATION_3, STATION_4);
        requestAndAssertLineWithAllStationsInOrderResponseDtos(expectedStations);
    }
}
