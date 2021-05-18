package wooteco.subway.section.web;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.web.LineRequest;
import wooteco.subway.station.web.StationRequest;
import wooteco.subway.station.web.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[API] 구간관련 테스트")
class SectionApiControllerTest extends AcceptanceTest {
    private static final int ORIGINAL_DISTANCE = 10;
    private StationRequest 잠실역;
    private StationRequest 잠실새내역;
    private StationRequest 강남역;
    private StationRequest 동탄역;
    private StationRequest 수서역;

    @BeforeEach
    void setUpStationRequest() {
        잠실역 = new StationRequest("잠실역");
        잠실새내역 = new StationRequest("잠실새내역");
        강남역 = new StationRequest("강남역");
        동탄역 = new StationRequest("동탄역");
        수서역 = new StationRequest("수서역");
    }

    @AfterEach
    void clear() {
        잠실역 = null;
        잠실새내역 = null;
        강남역 = null;
        동탄역 = null;
        수서역 = null;
    }

    @Test
    @DisplayName("구간 등록 - 성공(상행종점 등록)")
    void create_성공_상행종점추가() {
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        Long 강남역_id = postStationAndGetId(강남역);
        SectionRequest 강남_잠실 = new SectionRequest(강남역_id, 잠실역_id, 4);

        // when
        ExtractableResponse<Response> result = postSection(강남_잠실, lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());


        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(3);
        assertThat(stationsResult).containsExactly("강남역", "잠실역", "잠실새내역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행종점 등록)")
    void create_성공_하행종점추가() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        Long 강남역_id = postStationAndGetId(강남역);
        SectionRequest 잠실새내_강남 = new SectionRequest(잠실새내_id, 강남역_id, 4);

        // when
        ExtractableResponse<Response> result = postSection(잠실새내_강남, lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(3);
        assertThat(stationsResult).containsExactly("잠실역", "잠실새내역", "강남역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(상행기준 중간구간 구간 등록)")
    void create_성공_중간역상행기준() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);
        Long 강남역_id = postStationAndGetId(강남역);
        SectionRequest 잠실_강남 = new SectionRequest(잠실역_id, 강남역_id, 4);

        // when
        ExtractableResponse<Response> result = postSection(잠실_강남, lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(3);
        assertThat(stationsResult).containsExactly("잠실역", "강남역", "잠실새내역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(하행기준 중간구간 구간 등록)")
    void create_성공_중간하행기준() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        Long 강남역_id = postStationAndGetId(강남역);
        SectionRequest 강남_잠실새내 = new SectionRequest(강남역_id, 잠실새내_id, 4);

        // when
        ExtractableResponse<Response> result = postSection(강남_잠실새내, lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(3);
        assertThat(stationsResult).containsExactly("잠실역", "강남역", "잠실새내역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(중간 구간 등록 a-b-c-d --(b-k)--> a-b-k-c-d)")
    void create_성공_중간앞() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 강남역_id = postStationAndGetId(강남역);
        Long 수서역_id = postStationAndGetId(수서역);
        Long 동탄역_id = postStationAndGetId(동탄역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        postSection(new SectionRequest(강남역_id, 잠실역_id, 4), lineId);
        postSection(new SectionRequest(잠실새내_id, 동탄역_id, 4), lineId);

        // when
        ExtractableResponse<Response> result = postSection(new SectionRequest(잠실역_id, 수서역_id, 2), lineId);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(CREATED);
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(5);
        assertThat(stationsResult).containsExactly("강남역", "잠실역", "수서역", "잠실새내역", "동탄역");
    }

    @Test
    @DisplayName("구간 등록 - 성공(중간 구간 등록 a-b-c-d --(k-c)--> a-b-k-c-d)")
    void create_성공_중간뒤() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 강남역_id = postStationAndGetId(강남역);
        Long 수서역_id = postStationAndGetId(수서역);
        Long 동탄역_id = postStationAndGetId(동탄역);

        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        postSection(new SectionRequest(강남역_id, 잠실역_id, 4), lineId);
        postSection(new SectionRequest(잠실새내_id, 동탄역_id, 4), lineId);


        ExtractableResponse<Response> result = postSection(new SectionRequest(수서역_id, 잠실새내_id, 2), lineId);
        assertThat(result.statusCode()).isEqualTo(CREATED);
        ExtractableResponse<Response> lineResult = get("/lines/" + lineId);
        List<String> stationsResult = lineResult.jsonPath().getList("stations", StationResponse.class)
                .stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        // then
        assertThat(result.header("Location")).isNotNull();
        assertThat(stationsResult).hasSize(5);
        assertThat(stationsResult).containsExactly("강남역", "잠실역", "수서역", "잠실새내역", "동탄역");
    }

    @Test
    @DisplayName("구간 등록 - 실패(새로 추가할 거리가 기존 거리보다 같거나 큰 경우)")
    void create_실패_거리큼() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 강남역_id = postStationAndGetId(강남역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        ExtractableResponse<Response> result = postSection(new SectionRequest(강남역_id, 잠실새내_id, ORIGINAL_DISTANCE), lineId);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.body().asString()).isEqualTo("새로 추가할 거리가 기존 거리보다 같거나 큽니다.");
    }

    @Test
    @DisplayName("구간 등록 - 실패(존재하지 않는 역을 등록할 경우)")
    void create_실패_역없음() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        final SectionRequest 이상한구간 = new SectionRequest(Long.MAX_VALUE, 잠실새내_id, ORIGINAL_DISTANCE);

        // when
        ExtractableResponse<Response> result = postSection(이상한구간, lineId);

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("구간 등록 - 실패(자연수가 아닌 거리를 등록할 경우)")
    void create_실패_거리가자연수아님() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 강남역_id = postStationAndGetId(강남역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        final SectionRequest 강남_잠실새내 = new SectionRequest(강남역_id, 잠실새내_id, 0);

        // when
        ExtractableResponse<Response> result = postSection(강남_잠실새내, lineId);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.body().asString()).isEqualTo("자연수가 아닌 거리를 등록하셨습니다.");
    }

    @Test
    @DisplayName("구간 등록 - 실패(의미상 중복된 섹션을 등록할 경우)")
    void create_실패_의미상중복구간() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);
        final SectionRequest 잠실새내_잠실 = new SectionRequest(잠실새내_id, 잠실역_id, 3);

        //when
        ExtractableResponse<Response> result = postSection(잠실새내_잠실, lineId);

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.body().asString()).isEqualTo("중복된 구간입니다.");
    }

    @Test
    @DisplayName("구간 등록 - 실패(새로운 섹션의 두 역이 모두 같은 노선에 포함된 경우)")
    void create_실패_사이클도는구간() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 강남역_id = postStationAndGetId(강남역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 강남역_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        final SectionRequest 강남_잠실새내 = new SectionRequest(강남역_id, 잠실새내_id, 3);
        final SectionRequest 잠실_잠실새내 = new SectionRequest(잠실역_id, 잠실새내_id, 3);

        postSection(강남_잠실새내, lineId);

        // when
        ExtractableResponse<Response> result = postSection(잠실_잠실새내, lineId);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.body().asString()).isEqualTo("사이클이 생기는 구간입니다.");
    }

    @Test
    @DisplayName("구간 제거 - 성공")
    void delete_성공() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 강남역_id = postStationAndGetId(강남역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        SectionRequest 강남_잠실 =
                new SectionRequest(강남역_id, 잠실역_id, 4);
        postSection(강남_잠실, lineId);

        // when
        ExtractableResponse<Response> deleteResponse = delete("/lines/" + lineId + "/sections?stationId=" + 잠실역_id);
        ExtractableResponse<Response> result = get("/lines/" + lineId);
        List<StationResponse> stations = result.body().jsonPath().getList("stations", StationResponse.class);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(stations).hasSize(2);
        assertThat(stations.get(0).getName()).isEqualTo("강남역");
        assertThat(stations.get(1).getName()).isEqualTo("잠실새내역");
    }

    @Test
    @DisplayName("구간 제거 - 실패(노선이 존재하지 않을 시)")
    public void delete_실패_노선없음() {
        //given & when
        ExtractableResponse<Response> result = delete("/lines/" + Long.MAX_VALUE + "/sections?stationId=1");

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("구간 제거 - 실패(역이 해당 노선에 등록되어 있지 않을 시)")
    public void delete_실패_역이노선에없음() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);

        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        //when
        ExtractableResponse<Response> result = delete("/lines/" + lineId + "/sections?stationId=" + Long.MAX_VALUE);

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("구간 제거 - 실패(노선에 구간이 하나밖에 존재하지 않을 시)")
    public void delete_실패_마지막남은구간() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, ORIGINAL_DISTANCE);
        Long lineId = postLineAndGetId(이호선);

        //when
        ExtractableResponse<Response> result = delete("/lines/" + lineId + "/sections?stationId=" + 잠실새내_id);

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

}
