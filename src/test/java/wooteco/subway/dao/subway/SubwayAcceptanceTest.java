package wooteco.subway.dao.subway;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.controller.response.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.dao.fixture.Fixture.*;
import static wooteco.subway.dao.fixture.LineAcceptanceTestFixture.createLineWithSection;
import static wooteco.subway.dao.subway.SubwayAcceptanceTestFixture.*;

@Sql("classpath:tableInit.sql")
public class SubwayAcceptanceTest extends AcceptanceTest {
    @Test
    @DisplayName("노선 생성 - 노선을 생성한다.")
    void createLine() {
        // given - when
        ExtractableResponse<Response> response = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = response.header("Location");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(uri.split("/")[2]);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(LINE_NAME);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(LINE_COLOR);
    }

    @Test
    @DisplayName("노선 생성 - 상행 종점, 하행 종점이 모두 같게 하여 노선을 생성한다.")
    void createLineWithSameStations() {
        // given - when
        ExtractableResponse<Response> response = extractResponseWhenPost(createLineWithSection(STATIONS_SAME), "/lines"); // 노선 등록

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 조회 - 노선을 ID로 조회하여 포함된 모든 구간을 나타낸다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location");

        // when
        final ExtractableResponse<Response> response = extractResponseWhenGet(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(uri.split("/")[2]);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("1호선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-100");
        final List<StationResponse> stationResponses = response.jsonPath().getList("stations", StationResponse.class);
        for (int i = 0; i < stationResponses.size(); i++) {
            assertThat(stationResponses.get(i).getId()).isEqualTo(STATIONS1.get(i).getId());
            assertThat(stationResponses.get(i).getName()).isEqualTo(STATIONS1.get(i).getName());
        }
    }

    @DisplayName("구간 추가 - ID에 해당하는 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location") + "/sections";

        // when
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionRequest(), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(uri.split("/")[2]);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("1호선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-100");
        final List<StationResponse> stationResponses = response.jsonPath().getList("stations", StationResponse.class);
        assertThat(stationResponses.size()).isEqualTo(3);
    }

    @DisplayName("구간 추가 - 상행 종점을 추가한다.")
    @Test
    void expandUpStation() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location") + "/sections";

        // when
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionExpandUpStationRequest(), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(uri.split("/")[2]);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("1호선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-100");
        final List<StationResponse> stationResponses = response.jsonPath().getList("stations", StationResponse.class);
        assertThat(stationResponses.size()).isEqualTo(3);
    }

    @DisplayName("구간 추가 - 하행 종점을 추가한다.")
    @Test
    void expandDownStation() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location") + "/sections";

        // when
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionExpandDownStationRequest(), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().get("id").toString()).isEqualTo(uri.split("/")[2]);
        assertThat(response.body().jsonPath().getString("name")).isEqualTo("1호선");
        assertThat(response.body().jsonPath().getString("color")).isEqualTo("bg-red-100");
        final List<StationResponse> stationResponses = response.jsonPath().getList("stations", StationResponse.class);
        assertThat(stationResponses.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("구간 추가 - 기존 구간보다 거리가 길기 때문에 구간 추가가 불가능하다.")
    void addSectionWhenDistancesAreMismatch() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location") + "/sections";

        // when
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionWithLongDistanceRequest(), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("기존 구간과 상행 하행이 모두 동일하므로 구간 추가가 불가능하다.")
    void addSectionWhenEndSectionsAreSame() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location") + "/sections";

        // when
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionWithSameEndSectionsRequest(), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가 - 상행과 하행이 모두 노선에 포함되어있지 않은 역이므로 구간 추가가 불가능하다.")
    void addSectionWhenEndSectionsAreNotIncluded() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location") + "/sections";

        // when
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionRequestWithAnotherSection(), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 제거 - 노선 중 중간역을 삭제한다.")
    void deleteSectionInMiddle() {
        // given
        final ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        final String uri = createResponse.header("Location") + "/sections";
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionExpandUpStationRequest(), uri);

        // when
        final ExtractableResponse<Response> deleteResponse = extractResponseWhenDelete(uri + "?stationId=1");

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("구간 제거 - 노선 중 하행 종점을 삭제한다.")
    void deleteSectionInLast() {
        // given
        final ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        final String uri = createResponse.header("Location") + "/sections";
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionExpandUpStationRequest(), uri);

        // when
        final ExtractableResponse<Response> deleteResponse = extractResponseWhenDelete(uri + "?stationId=2");

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("구간 제거 - 노선 중 상행 종점을 삭제한다.")
    void deleteSectionInFirst() {
        // given
        final ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        final String uri = createResponse.header("Location") + "/sections";
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionExpandUpStationRequest(), uri);

        // when
        final ExtractableResponse<Response> deleteResponse = extractResponseWhenDelete(uri + "?stationId=3");

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("구간 제거 - 노선에 구간이 1개인 경우에는 삭제가 불가능하다.")
    void deleteSectionInException() {
        // given
        final ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        final String uri = createResponse.header("Location") + "/sections";

        // when
        final ExtractableResponse<Response> deleteResponse = extractResponseWhenDelete(uri + "?stationId=2");

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
