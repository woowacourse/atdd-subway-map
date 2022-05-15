package wooteco.subway.acceptance;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간을 생성한다.")
    @Test
    void saveSection() {
        createStationForTest("강남역");
        createStationForTest("선릉역");
        createStationForTest("잠실역");

        ExtractableResponse<Response> createLineResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> response = RequestFrame.post(
            makeBodyForPost("2", "3", "10"),
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections"
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("등록되지 않은 id의 자하철 노선에 등록 요청한다.(400 에러)")
    @Test
    void saveSection_withNotExistLineId() {
        createStationForTest("강남역");
        createStationForTest("선릉역");
        createStationForTest("잠실역");

        ExtractableResponse<Response> createLineResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> response = RequestFrame.post(
            makeBodyForPost("2", "3", "10"),
            "/lines/" + 2 + "/sections"
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역, 하행역이 이미 노선에 있는 구간을 등록 요청한다.(400에러)")
    @Test
    void saveSection_withAlreadyExistSection() {
        createStationForTest("강남역");
        createStationForTest("선릉역");
        createStationForTest("잠실역");

        ExtractableResponse<Response> createLineResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> createSectionResponse = RequestFrame.post(
            makeBodyForPost("2", "3", "10"),
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections"
        );

        ExtractableResponse<Response> response = RequestFrame.post(
            makeBodyForPost("1", "3", "20"),
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections"
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역, 하행역이 둘다 노선에 없는 구간을 등록 요청한다.(400에러)")
    @Test
    void saveSection_withNotExistUpAndDownStationBoth() {
        createStationForTest("강남역");
        createStationForTest("선릉역");

        ExtractableResponse<Response> createLineResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        Map<String, String> params = new HashMap<>();
        params.put("upStationId", "3");
        params.put("downStationId", "4");
        params.put("distance", "10");

        ExtractableResponse<Response> response = RequestFrame.post(
            makeBodyForPost("3", "4", "10"),
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections"
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존 구간의 내부에 더 긴 구간을 등록 요청한다.(400에러)")
    @Test
    void saveSection_withBiggerInnerSection() {
        createStationForTest("강남역");
        createStationForTest("선릉역");
        createStationForTest("잠실역");

        ExtractableResponse<Response> createLineResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> response = RequestFrame.post(
            makeBodyForPost("1", "3", "10"),
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections"
        );

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteSection() {
        createStationForTest("강남역");
        createStationForTest("선릉역");
        createStationForTest("잠실역");

        ExtractableResponse<Response> createLineResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> createdResponse = RequestFrame.post(
            makeBodyForPost("2", "3", "10"),
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections"
        );

        ExtractableResponse<Response> response = RequestFrame.delete(
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections?stationId=2");
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선에 구간이 한개인 경우 구간 삭제 요청한다.(400에러)")
    @Test
    void deleteSection_justOneSectionExistsInLine() {
        createStationForTest("강남역");
        createStationForTest("선릉역");

        ExtractableResponse<Response> createLineResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> response = RequestFrame.delete(
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections?stationId=2");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선이 지나지 않는 역을 포함한 구간 삭제 요청한다.(400에러)")
    @Test
    void deleteSection_notExistStationInLine() {
        createStationForTest("강남역");
        createStationForTest("선릉역");

        ExtractableResponse<Response> createLineResponse = RequestFrame.post(
            BodyCreator.makeLineBodyForPost("2호선", "green", "1", "2", "10"),
            "/lines"
        );

        ExtractableResponse<Response> response = RequestFrame.delete(
            "/lines/" + createLineResponse.jsonPath().getLong("id") + "/sections?stationId=3");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void createStationForTest(String stationName) {
        ExtractableResponse<Response> response = RequestFrame.post(
            BodyCreator.makeStationBodyForPost(stationName),
            "/stations"
        );
    }

    private Map<String, String> makeBodyForPost(String upStationId, String downStationId, String distance) {
        Map<String, String> body = new HashMap<>();
        body.put("upStationId", upStationId);
        body.put("downStationId", downStationId);
        body.put("distance", distance);
        return body;
    }
}
