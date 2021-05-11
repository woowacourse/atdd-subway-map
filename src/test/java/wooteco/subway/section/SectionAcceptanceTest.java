package wooteco.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;
import wooteco.subway.station.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.RequestForm.createRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("존재하는 노선 중간에 지하철 구간을 생성한다(ex. A - B가 존재할 때 A - C를 넣는 경우")
    @Test
    void createSectionCaseOne() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 10);
        SectionCreateRequest 강남_왕십리 = new SectionCreateRequest(1L, 3L, 5);

        // when
        createRequest("/stations", 강남역);
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response =
                createRequest("/lines/" + 분당선생성.jsonPath().getLong("id") + "/sections", 강남_왕십리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.as(SectionCreateResponse.class))
                .usingRecursiveComparison()
                .isEqualTo(new SectionCreateResponse(2L, 1L, 1L, 3L, 5));
    }

    @DisplayName("존재하는 노선 중간에 지하철 구간을 생성한다(ex. A - B가 존재할 때 C - B를 넣는 경우")
    @Test
    void createSectionCaseTwo() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 10);
        SectionCreateRequest 왕십리_잠실 = new SectionCreateRequest(3L, 2L, 5);

        // when
        createRequest("/stations", 강남역);
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response =
                createRequest("/lines/" + 분당선생성.jsonPath().getLong("id") + "/sections", 왕십리_잠실);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.as(SectionCreateResponse.class))
                .usingRecursiveComparison()
                .isEqualTo(new SectionCreateResponse(2L, 1L, 3L, 2L, 5));
    }

    @DisplayName("존재하는 노선에 상행 종점을 추가한다")
    @Test
    void createSectionUpStation() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 10);
        SectionCreateRequest 왕십리_강남 = new SectionCreateRequest(3L, 1L, 5);

        // when
        createRequest("/stations", 강남역);
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response =
                createRequest("/lines/" + 분당선생성.jsonPath().getLong("id") + "/sections", 왕십리_강남);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.as(SectionCreateResponse.class))
                .usingRecursiveComparison()
                .isEqualTo(new SectionCreateResponse(2L, 1L, 3L, 1L, 5));
    }

    @DisplayName("존재하는 노선에 하행 종점을 추가한다")
    @Test
    void createSectionDownStation() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 10);
        SectionCreateRequest 잠실_왕십리 = new SectionCreateRequest(2L, 3L, 5);

        // when
        createRequest("/stations", 강남역);
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response =
                createRequest("/lines/" + 분당선생성.jsonPath().getLong("id") + "/sections", 잠실_왕십리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.as(SectionCreateResponse.class))
                .usingRecursiveComparison()
                .isEqualTo(new SectionCreateResponse(2L, 1L, 2L, 3L, 5));
    }

    @DisplayName("존재하는 노선에 지하철 구간을 생성할 때 구간의 양 끝점이 모두 노선에 포함되지 않은 경우 BAD_REQUEST 반환")
    @Test
    void createSectionNotIncludeBothEndPoint() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");
        StationRequest 뚝섬역 = new StationRequest("뚝섬역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 10);
        SectionCreateRequest 왕십리_뚝섬 = new SectionCreateRequest(3L, 4L, 5);

        // when
        createRequest("/stations", 강남역);
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        createRequest("/stations", 뚝섬역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response =
                createRequest("/lines/" + 분당선생성.jsonPath().getLong("id") + "/sections", 왕십리_뚝섬);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하는 노선에 지하철 구간을 생성할 때 구간의 양 끝점이 모두 노선에 포함된 경우 BAD_REQUEST 반환")
    @Test
    void createSectionIncludeBothEndPoint() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 10);
        SectionCreateRequest 강남_잠실 = new SectionCreateRequest(1L, 2L, 5);

        // when
        createRequest("/stations", 강남역);
        createRequest("/stations", 잠실역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response =
                createRequest("/lines/" + 분당선생성.jsonPath().getLong("id") + "/sections", 강남_잠실);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하는 노선에 지하철 구간을 생성할 때 추가하려는 구간의 거리가 삽입하려는 구간보다 길거나 같은 경우 BAD_REQUEST 반환")
    @Test
    void createSectionLongDistance() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        StationRequest 왕십리역 = new StationRequest("왕십리역");
        LineCreateRequest 분당선_RED =
                new LineCreateRequest("분당선", "bg-red-600", 1L, 2L, 10);
        SectionCreateRequest 강남_왕십리 = new SectionCreateRequest(3L, 4L, 10);

        // when
        createRequest("/stations", 강남역);
        createRequest("/stations", 잠실역);
        createRequest("/stations", 왕십리역);
        ExtractableResponse<Response> 분당선생성 = createRequest("/lines", 분당선_RED);
        ExtractableResponse<Response> response =
                createRequest("/lines/" + 분당선생성.jsonPath().getLong("id") + "/sections", 강남_왕십리);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
