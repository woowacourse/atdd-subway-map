package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class SectionAcceptanceTest extends AcceptanceTest {

    private Long 선릉역_id;
    private Long 선정릉역_id;
    private Long 한티역_id;
    private Long 모란역_id;

    private Long 분당선_id;

    private String createPath;
    private String deletePath;

    @BeforeEach
    void setUpStations() {
        선릉역_id = RestAssuredConvenienceMethod.postStationAndGetId(new StationRequest("선릉역"), "/stations");
        선정릉역_id = RestAssuredConvenienceMethod.postStationAndGetId(new StationRequest("선정릉역"), "/stations");
        한티역_id = RestAssuredConvenienceMethod.postStationAndGetId(new StationRequest("한티역"), "/stations");
        모란역_id = RestAssuredConvenienceMethod.postStationAndGetId(new StationRequest("모란역"), "/stations");

        분당선_id = RestAssuredConvenienceMethod.postLineAndGetId(
                new LineRequest("분당선", "yellow", 선릉역_id, 선정릉역_id, 10), "/lines");

        createPath = "/lines/" + 분당선_id + "/sections";
        deletePath = "/lines/" + 분당선_id + "/sections?stationId=";
    }

    @DisplayName("상행 종점에 역을 등록한다.")
    @Test
    void createStationAtLastUp() {
        // given
        SectionRequest request = new SectionRequest(모란역_id, 선릉역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, createPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점에 역을 등록한다.")
    @Test
    void createStationAtLastDown() {
        // given
        SectionRequest request = new SectionRequest(선정릉역_id, 모란역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, createPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간 사이에 새로운 구간을 등록한다.")
    @Test
    void createStationAtMiddle() {
        // given
        SectionRequest request = new SectionRequest(선릉역_id, 모란역_id, 7);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, createPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 역으로 구간 등록을 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionWithNonExistStation() {
        // given
        SectionRequest request = new SectionRequest(100L, 선정릉역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, createPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 포함되지 않은 두 역으로 구간을 만들려고 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionNotContainStationInLine() {
        // given
        SectionRequest request = new SectionRequest(모란역_id, 한티역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, createPath);

        // when
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("이미 노선에 존재하는 두 역으로 구간을 만들려고 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionContainExistTwoStationInLine() {
        // given
        SectionRequest request = new SectionRequest(선릉역_id, 선정릉역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, createPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("0이하의 거리로 구간 생성을 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionWithNegativeDistance() {
        // given
        SectionRequest request = new SectionRequest(한티역_id, 선정릉역_id, 0);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, createPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간 사이에 새로운 구간을 등록할 때, 기존 구간보다 큰 거리로 생성을 시도하면 400번 코드가 반환된다.")
    @Test
    void throwsExceptionWithLongerDistance() {
        // given
        SectionRequest request = new SectionRequest(선릉역_id, 선정릉역_id, 10);

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.postRequest(request, createPath);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행 종점 역이 포함된 구간을 올바르게 삭제한다.")
    @Test
    void deleteSectionAtLastUpStation() {
        // given
        RestAssuredConvenienceMethod.postRequest(
                new SectionRequest(선정릉역_id, 한티역_id, 10), "/lines/" + 분당선_id + "/sections");

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + 선릉역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("하행 종점 역이 포함된 구간을 올바르게 삭제한다.")
    @Test
    void deleteSectionAtLastDownStation() {
        // given
        RestAssuredConvenienceMethod.postRequest(
                new SectionRequest(선정릉역_id, 한티역_id, 10), "/lines/" + 분당선_id + "/sections");

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + 한티역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("중간 역이 포함된 구간을 올바르게 삭제한다.")
    @Test
    void deleteSectionAtMiddleStation() {
        // given
        RestAssuredConvenienceMethod.postRequest(
                new SectionRequest(선정릉역_id, 한티역_id, 10), "/lines/" + 분당선_id + "/sections");

        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + 선정릉역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선의 역을 삭제할 때 구간이 하나만 남았을 경우 400번 코드가 반환된다.")
    @Test
    void throwsExceptionWithOneRemainSection() {
        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + 선정릉역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("현재 라인에 존재하지 않는 역으로 삭제 시도시 예외가 발생한다.")
    @Test
    void throwsExceptionWithNotExistStationInLine() {
        // when
        ExtractableResponse<Response> response =
                RestAssuredConvenienceMethod.deleteRequest(deletePath + 한티역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
