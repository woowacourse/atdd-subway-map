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
import static wooteco.subway.dao.fixture.CommonFixture.extractResponseWhenGet;
import static wooteco.subway.dao.fixture.CommonFixture.extractResponseWhenPost;
import static wooteco.subway.dao.fixture.DomainFixture.STATIONS1;
import static wooteco.subway.dao.fixture.DomainFixture.STATIONS2;
import static wooteco.subway.dao.fixture.LineAcceptanceTestFixture.*;
import static wooteco.subway.dao.subway.SubwayAcceptanceTestFixture.*;

@Sql("classpath:tableInit.sql")
public class SubwayAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선을 ID로 조회하여 포함된 모든 구간을 나타낸다.")
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

    @DisplayName("ID에 해당하는 노선에 구간을 추가한다.")
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

    @DisplayName("상행 종점을 추가한다.")
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

    @DisplayName("하행 종점을 추가한다.")
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
    @DisplayName("기존 구간보다 거리가 길기 때문에 구간 추가가 불가능하다.")
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
    @DisplayName("상행과 하행이 모두 노선에 포함되어있지 않은 역이므로 구간 추가가 불가능하다.")
    void addSectionWhenEndSectionsAreNotIncluded() {
        // given
        ExtractableResponse<Response> createResponse = extractResponseWhenPost(createLineWithSection(STATIONS1), "/lines"); // 노선 등록
        String uri = createResponse.header("Location") + "/sections";

        // when
        final ExtractableResponse<Response> response = extractResponseWhenPost(createAddSectionRequestWithAnotherSection(), uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
