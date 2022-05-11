package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.AcceptanceFixture.LINE_2_요청;
import static wooteco.subway.acceptance.AcceptanceFixture.LINE_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.SECTION_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.STATION_URL;
import static wooteco.subway.acceptance.AcceptanceFixture.deleteSectionMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.getMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.postMethodRequest;
import static wooteco.subway.acceptance.AcceptanceFixture.강남역_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.서울숲역_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.역삼역_인자;
import static wooteco.subway.acceptance.AcceptanceFixture.왕십리역_인자;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

public class SectionAcceptanceTest extends AcceptanceTest {

    private int lineId;

    @BeforeEach
    void setUpLine() {
        postMethodRequest(강남역_인자, STATION_URL);
        postMethodRequest(역삼역_인자, STATION_URL);
        ExtractableResponse<Response> lineResponse = postMethodRequest(LINE_2_요청, LINE_URL);
        lineId = lineResponse.body().jsonPath().get("id");
    }

    @Test
    @DisplayName("상행 지점에 가지 구간이 생기면 추가한다.")
    void addSectionToUpStation() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                1L,
                3L,
                3
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        postMethodRequest(sectionRequest, url);
        ExtractableResponse<Response> lineResponse = getMethodRequest(LINE_URL + "/" + lineId);

        assertAll(
                () -> assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> {
                    List<StationResponse> stations = lineResponse.body().jsonPath().get("stations");
                    assertThat(stations.size()).isEqualTo(3);
                }
        );
    }

    @Test
    @DisplayName("상행 지점에 가지 구간 추가 요청 시 거리가 기존의 거리보다 멀면 예외를 반환한다.")
    void addSectionToUpStationWithLongerDistance() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                1L,
                3L,
                6
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        ExtractableResponse<Response> response = postMethodRequest(sectionRequest, url);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains("5", "6");
    }


    @Test
    @DisplayName("하행 지점에 가지 구간이 생기면 추가한다.")
    void addSectionDownStation() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                3L,
                2L,
                3
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        postMethodRequest(sectionRequest, url);
        ExtractableResponse<Response> lineResponse = getMethodRequest(LINE_URL + "/" + lineId);

        assertAll(
                () -> assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> {
                    List<StationResponse> stations = lineResponse.body().jsonPath().get("stations");
                    assertThat(stations.size()).isEqualTo(3);
                }
        );
    }

    @Test
    @DisplayName("하행 지점에 가지 구간 추가 요청 시 거리가 기존의 거리보다 멀면 예외를 반환한다.")
    void addSectionToDownStationWithLongerDistance() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                3L,
                2L,
                6
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        ExtractableResponse<Response> response = postMethodRequest(sectionRequest, url);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains("5", "6");
    }

    @Test
    @DisplayName("상행 지점에 종점 구간이 생기면 추가한다.")
    void addSectionLastUpStation() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                3L,
                1L,
                3
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        postMethodRequest(sectionRequest, url);
        ExtractableResponse<Response> lineResponse = getMethodRequest(LINE_URL + "/" + lineId);

        assertAll(
                () -> assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> {
                    List<StationResponse> stations = lineResponse.body().jsonPath().get("stations");
                    assertThat(stations.size()).isEqualTo(3);
                }
        );
    }

    @Test
    @DisplayName("하행 지점에 종점 구간이 생기면 추가한다.")
    void addSectionLastDownStation() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                2L,
                3L,
                3
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        postMethodRequest(sectionRequest, url);
        ExtractableResponse<Response> lineResponse = getMethodRequest(LINE_URL + "/" + lineId);

        assertAll(
                () -> assertThat(lineResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> {
                    List<StationResponse> stations = lineResponse.body().jsonPath().get("stations");
                    assertThat(stations.size()).isEqualTo(3);
                }
        );
    }

    @Test
    @DisplayName("추가할 구간의 두 개의 역이 같으면 예외를 반환한다.")
    void addSectionWithSameStations() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                1L,
                2L,
                3
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        ExtractableResponse<Response> response = postMethodRequest(sectionRequest, url);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains("강남역", "역삼역");
    }

    @Test
    @DisplayName("추가할 구간의 두 개의 역이 없으면 예외를 반환한다.")
    void addSectionWithNonExistStations() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        postMethodRequest(서울숲역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                3L,
                4L,
                3
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        ExtractableResponse<Response> response = postMethodRequest(sectionRequest, url);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.asString()).contains("추가할 수");
    }

    @Test
    @DisplayName("삭제")
    void deleteSection() {
        postMethodRequest(왕십리역_인자, STATION_URL);
        SectionRequest sectionRequest = new SectionRequest(
                1L,
                3L,
                3
        );
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        postMethodRequest(sectionRequest, url);
        ExtractableResponse<Response> response = deleteSectionMethodRequest(url, 3);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("삭제")
    void deleteSectionWithLastSection() {
        String url = LINE_URL + "/" + lineId + SECTION_URL;
        ExtractableResponse<Response> response = deleteSectionMethodRequest(url, 2);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
