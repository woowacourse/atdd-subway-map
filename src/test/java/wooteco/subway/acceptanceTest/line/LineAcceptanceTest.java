package wooteco.subway.acceptanceTest.line;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.assertLineResponseDto;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.requestAndGetAllSavedLineResponseDtosInOrder;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.requestAndGetAllSavedLinesIds;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.requestAndGetSingleSavedLineResponseDto;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.requestCreateLineAndGetResponse;
import static wooteco.subway.acceptanceTest.line.LineAcceptanceTestUtils.requestUpdateLineAndGetResponse;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.DEFAULT_SECTION_DISTANCE;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.LINE_COLOR;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.LINE_ID;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.LINE_NAME;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.STATION_1;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.STATION_2;
import static wooteco.subway.acceptanceTest.section.SectionAcceptanceTestUtils.requestCreateAndSetLineWithSectionsAndGetResponse;

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

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void createLine() {
        // given, when
        ExtractableResponse<Response> response
            = requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(STATION_1, STATION_2));

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
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(STATION_1, STATION_2));
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response
            = requestCreateLineAndGetResponse(LINE_NAME, LINE_COLOR + 1, 100L, 200L, 200);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("기존에 존재하는 색깔로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        requestCreateAndSetLineWithSectionsAndGetResponse(Arrays.asList(STATION_1, STATION_2));
        Long savedLineIdBeforeRequest = requestAndGetAllSavedLinesIds().get(0);

        // when
        ExtractableResponse<Response> response
            = requestCreateLineAndGetResponse(LINE_NAME + 1, LINE_COLOR, 100L, 200L, 200);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allSavedLineIds = requestAndGetAllSavedLinesIds();
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("Id로 하나의 노선을 조회한다.")
    @Test
    void getOneLineById() {
        // given
        String lineNameToCreate = "신분당선";
        String lineColorToCreate = "bg-red-600";
        requestCreateLineAndGetResponse(lineNameToCreate, lineColorToCreate);
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

        assertThat(response.body().jsonPath().getString("id")).isEqualTo("1");
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(lineNameToCreate);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(lineColorToCreate);
    }

    @DisplayName("모든 노선들을 조회한다.")
    @Test
    void getAllLines() {
        /// given
        String firstLineName = "신분당선";
        String firstLineColor = "bg-red-600";
        requestCreateLineAndGetResponse(firstLineName, firstLineColor);
        String secondLineName = "2호선";
        String secondLineColor = "bg-green-600";
        requestCreateLineAndGetResponse(secondLineName, secondLineColor);

        // when
        List<LineResponseDto> lineResponseDtosInOrder = requestAndGetAllSavedLineResponseDtosInOrder();

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
        requestCreateLineAndGetResponse("신분당선", "bg-red-600");
        Long lineIdToUpdate = requestAndGetAllSavedLinesIds().get(0);

        // when
        String newLineName = "구분당선";
        String newColor = "bg-blue-600";
        ExtractableResponse<Response> response = requestUpdateLineAndGetResponse(lineIdToUpdate, newLineName, newColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponseDto updatedLineResponseDto = requestAndGetAllSavedLineResponseDtosInOrder().get(0);
        assertLineResponseDto(updatedLineResponseDto, 1L, newLineName, newColor);
    }

    @DisplayName("이미 존재하는 이름으로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateName() {
        // given
        requestCreateLineAndGetResponse("신분당선", "bg-green-600");

        String oldLineName = "2호선";
        String oldColor = "bg-red-600";
        ExtractableResponse<Response> oldLineResponse = requestCreateLineAndGetResponse(oldLineName, oldColor);
        Long lineIdToUpdate = Long.parseLong(oldLineResponse.header("Location").split("/")[2]);

        // when
        String newLineName = "신분당선";
        String newColor = "bg-blue-600";
        ExtractableResponse<Response> response = requestUpdateLineAndGetResponse(lineIdToUpdate, newLineName, newColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        LineResponseDto savedLineResponseDto = requestAndGetSingleSavedLineResponseDto(lineIdToUpdate);
        assertLineResponseDto(savedLineResponseDto,
            savedLineResponseDto.getId(), savedLineResponseDto.getName(), savedLineResponseDto.getColor());
    }

    @DisplayName("이미 존재하는 색깔로 노선을 수정한다.")
    @Test
    void updateLineWithDuplicateColor() {
        // given
        requestCreateLineAndGetResponse("신분당선", "bg-green-600");

        String oldLineName = "2호선";
        String oldColor = "bg-red-600";
        ExtractableResponse<Response> oldLineResponse = requestCreateLineAndGetResponse(oldLineName, oldColor);
        Long lineIdToUpdate = Long.parseLong(oldLineResponse.header("Location").split("/")[2]);

        // when
        String newLineName = "2호선";
        String newColor = "bg-green-600";

        ExtractableResponse<Response> response = requestUpdateLineAndGetResponse(lineIdToUpdate, newLineName, newColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        LineResponseDto savedLineResponseDto = requestAndGetSingleSavedLineResponseDto(lineIdToUpdate);
        assertLineResponseDto(savedLineResponseDto,
            savedLineResponseDto.getId(), savedLineResponseDto.getName(), savedLineResponseDto.getColor());
    }

    @DisplayName("노선을 Id로 제거한다.")
    @Test
    void deleteLineById() {
        // given
        requestCreateLineAndGetResponse("신분당선", "bg-red-600");
        requestCreateLineAndGetResponse("2호선", "bg-green-600");
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