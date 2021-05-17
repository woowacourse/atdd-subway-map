package wooteco.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.line.response.LineResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.fixture.DomainFixtures.*;
import static wooteco.subway.line.LineAcceptanceTestUtils.*;
import static wooteco.subway.section.SectionAcceptanceTestUtils.createLineWithSections;

@DisplayName("노선 관련 기능")
@Sql("classpath:tableInit.sql")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final List<Station> STATIONS = Arrays.asList(STATION_1, STATION_2, STATION_3, STATION_4);

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given - when
        ExtractableResponse<Response> response = createLineWithSections(STATIONS);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).isEqualTo("/lines/" + LINE_ID);

        assertThat(response.body().jsonPath().getLong("id")).isEqualTo(LINE_ID);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_NAME);
    }

    @DisplayName("기존에 존재하는 노선 이름으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        createLineWithSections(STATIONS);
        Long savedLineIdBeforeRequest = requestAndGetAllLineIds().get(0);

        // when
        ExtractableResponse<Response> response = createLineWithSections(LINE_NAME, LINE_COLOR + 1, STATIONS);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        List<Long> allLineIds = requestAndGetAllLineIds();
        assertThat(allLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("기존에 존재하는 색으로 노선을 생성한다.")
    @Test
    void createLineWithDuplicateColor() {
        // given
        createLineWithSections(STATIONS);
        Long savedLineIdBeforeRequest = requestAndGetAllLineIds().get(0);

        // when
        ExtractableResponse<Response> response = createLineWithSections(LINE_NAME + 1, LINE_COLOR, STATIONS);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        List<Long> allSavedLineIds = requestAndGetAllLineIds();
        assertThat(allSavedLineIds).containsExactly(savedLineIdBeforeRequest);
    }

    @DisplayName("노선들을 조회한다.")
    @Test
    void getLines() {
        /// given
        String 신분당선 = "신분당선";
        String 빨강 = "bg-red-600";
        createLineWithSections(신분당선, 빨강, STATIONS);
        String 경춘선 = "경춘선";
        String 초록 = "bg-green-600";
        createLineWithSections(경춘선, 초록, STATIONS);

        // when
        List<LineResponse> lineResponseInOrder = getAllLinesInIdOrder();

        // then
        LineResponse firstLineResponse = lineResponseInOrder.get(0);
        assertLineResponseDto(firstLineResponse, 1L, 신분당선, 빨강);

        LineResponse secondLineResponse = lineResponseInOrder.get(1);
        assertLineResponseDto(secondLineResponse, 2L, 경춘선, 초록);
    }

    @DisplayName("특정 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        createLineWithSections(STATIONS);
        Long savedLineId = requestAndGetAllLineIds().get(0);

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

    @DisplayName("노선 업데이트한다.")
    @Test
    void updateLine() {
        // given
        createLineWithSections(STATIONS);
        Long lineIdToUpdate = requestAndGetAllLineIds().get(0);

        // when
        String newLineName = "수정된분당선";
        String newColor = "bg-blue-600";
        ExtractableResponse<Response> response = requestUpdateLine(lineIdToUpdate, newLineName, newColor);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse updatedLineResponse = getAllLinesInIdOrder().get(0);
        assertLineResponseDto(updatedLineResponse, LINE_ID, newLineName, newColor);
    }

    @DisplayName("노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        createLineWithSections("신분당선", "bg-red-600", STATIONS);
        createLineWithSections("2호선", "bg-green-600", STATIONS);
        List<Long> allSavedLineIdsBeforeDelete = requestAndGetAllLineIds();
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

        List<Long> allSavedStationIdsAfterDelete = requestAndGetAllLineIds();
        assertThat(allSavedStationIdsAfterDelete).containsExactly(lineIdNotToDelete);
    }
}
