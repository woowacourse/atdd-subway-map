package wooteco.subway.acceptanceTest.line;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.DEFAULT_SECTION_DISTANCE;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.LINE_COLOR;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.LINE_ID;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.LINE_NAME;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.NEW_STATION;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.STATION_1;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.STATION_2;
import static wooteco.subway.acceptanceTest.fixture.DomainFixtures.STATION_3;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.assertLineResponseDto;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.getAllLinesInIdOrder;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.requestAndGetAllSavedLinesIds;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.requestUpdateLine;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.createLineWithSectionsOf;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.acceptanceTest.AcceptanceTest;
import wooteco.subway.controller.dto.response.line.LineResponseDto;
import wooteco.subway.domain.station.Station;

@DisplayName("노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {
    private static final List<Station> STATIONS = Arrays.asList(STATION_1, STATION_2);

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given, when
        ExtractableResponse<Response> response = createLineWithSectionsOf(STATIONS);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).isEqualTo("/lines/" + LINE_ID);

        assertThat(response.body().jsonPath().getLong("id")).isEqualTo(LINE_ID);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_NAME);
        assertThat(response.body().jsonPath().getLong("upStationId")).isEqualTo(STATION_1.getId());
        assertThat(response.body().jsonPath().getLong("downStationId")).isEqualTo(STATION_2.getId());
        assertThat(response.body().jsonPath().getInt("distance")).isEqualTo(DEFAULT_SECTION_DISTANCE);
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        createLineWithSectionsOf(STATIONS);
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = createLineWithSectionsOf(LINE_NAME, LINE_COLOR + 1, STATIONS);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("기존에 존재하는 색깔로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        createLineWithSectionsOf(STATIONS);
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = createLineWithSectionsOf(LINE_NAME + 1, LINE_COLOR, STATIONS);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("존재하지 않는 상행 종점역으로 노선을 생성한다.")
    @Test
    void createLineWithUpStationNotExists() {
        // given
        createLineWithSectionsOf(STATIONS);
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtils
            .createLine(LINE_NAME + 1, LINE_COLOR + 1, NEW_STATION.getId(), STATION_1.getId(), DEFAULT_SECTION_DISTANCE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("존재하지 않는 하행 종점역으로 노선을 생성한다.")
    @Test
    void createLineWithDownStationNotExists() {
        // given
        createLineWithSectionsOf(STATIONS);
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = LineAcceptanceTestUtils
            .createLine(LINE_NAME + 1, LINE_COLOR + 1, STATION_1.getId(), NEW_STATION.getId(), DEFAULT_SECTION_DISTANCE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("같은 상행 종점역과 하행 종점역으로 노선을 생성한다.")
    @Test
    void createLineWithSameUpStationAndDownStation() {
        // given
        createLineWithSectionsOf(STATIONS);
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = createLineWithSectionsOf(LINE_NAME + 1, LINE_COLOR, Arrays.asList(STATION_3, STATION_3));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("Id로 하나의 노선을 조회한다.")
    @Test
    void getOneLineById() {
        // given
        createLineWithSectionsOf(STATIONS);
        Long savedLineId = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .pathParam("id", savedLineId)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/{id}")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        assertThat(response.body().jsonPath().getLong("id")).isEqualTo(LINE_ID);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_NAME);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(LINE_COLOR);
    }

    @DisplayName("모든 노선들을 조회한다.")
    @Test
    void getAllLines() {
        /// given
        String firstLineName = "신분당선";
        String firstLineColor = "bg-red-600";
        createLineWithSectionsOf(firstLineName, firstLineColor, STATIONS);
        String secondLineName = "2호선";
        String secondLineColor = "bg-green-600";
        createLineWithSectionsOf(secondLineName, secondLineColor, STATIONS);

        // when
        List<LineResponseDto> lineResponseDtosInOrder = getAllLinesInIdOrder();

        // then
        LineResponseDto firstLineResponseDto = lineResponseDtosInOrder.get(0);
        assertLineResponseDto(firstLineResponseDto, 1L, firstLineName, firstLineColor);

        LineResponseDto secondLineResponseDto = lineResponseDtosInOrder.get(1);
        assertLineResponseDto(secondLineResponseDto, 2L, secondLineName, secondLineColor);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        createLineWithSectionsOf(STATIONS);
        Long lineIdToUpdate = requestAndGetAllSavedLinesIds().get(0);

        // when
        String newLineName = "구분당선";
        String newColor = "bg-blue-600";
        ExtractableResponse<Response> response = requestUpdateLine(lineIdToUpdate, newLineName, newColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponseDto updatedLineResponseDto = getAllLinesInIdOrder().get(0);
        assertLineResponseDto(updatedLineResponseDto, LINE_ID, newLineName, newColor);
    }

    @DisplayName("노선을 Id로 제거한다.")
    @Test
    void deleteLineById() {
        // given
        createLineWithSectionsOf("신분당선", "bg-red-600", STATIONS);
        createLineWithSectionsOf("2호선", "bg-green-600", STATIONS);
        List<Long> allSavedLineIdsBeforeDelete = requestAndGetAllSavedLinesIds();
        Long lineIdToDelete = allSavedLineIdsBeforeDelete.get(0);
        Long lineIdNotToDelete = allSavedLineIdsBeforeDelete.get(1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete("/lines/{id}", lineIdToDelete)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        List<Long> allSavedStationIdsAfterDelete = requestAndGetAllSavedLinesIds();
        assertThat(allSavedStationIdsAfterDelete).containsExactly(lineIdNotToDelete);
    }
}