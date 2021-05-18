package wooteco.subway.line.web;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.station.web.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[API] 노선 관련 테스트")
class LineApiControllerTest extends AcceptanceTest {
    private StationRequest 잠실역;
    private StationRequest 잠실새내역;
    private StationRequest 노원역;

    @BeforeEach
    void setUpStationRequest() {
        잠실역 = new StationRequest("잠실역");
        잠실새내역 = new StationRequest("잠실새내역");
        노원역 = new StationRequest("노원역");
    }

    @AfterEach
    void clear(){
        잠실역 = null;
        잠실새내역 = null;
        노원역 = null;
    }

    @Test
    @DisplayName("노선 생성 - 성공")
    void create_성공() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);

        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, 10);

        // when
        ExtractableResponse<Response> result = postLine(이호선);

        // then
        assertThat(result.header("Location")).isNotNull();
        assertThat(result.jsonPath().getLong("id")).isNotNull();
        assertThat(result.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(result.jsonPath().getString("color")).isEqualTo("bg-green-600");
        assertThat(result.jsonPath().getList("stations")).hasSize(2);
        assertThat(result.jsonPath().getString("stations[0].name")).isEqualTo("잠실역");
        assertThat(result.jsonPath().getString("stations[1].name")).isEqualTo("잠실새내역");
    }

    @Test
    @DisplayName("노선 생성 - 실패(노선 중복 이름)")
    void create_실패_중복이름() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 노원_id = postStationAndGetId(노원역);
        final LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, 10);
        postLine(이호선);

        // when
        final LineRequest 중복노선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 노원_id, 10);
        ExtractableResponse<Response> result = postLine(중복노선);

        // then
        assertThat(result.statusCode()).isEqualTo(BAD_REQUEST);
        assertThat(result.body().asString()).isEqualTo("중복되는 라인 정보가 존재합니다.");
    }

    @Test
    @DisplayName("노선 생성 - 실패(노선 중복 컬러)")
    void create_실패_중복색상() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        Long 노원_id = postStationAndGetId(노원역);
        LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실새내_id, 10);
        postLine(이호선);
        LineRequest 일호선 =
                new LineRequest("1호선", "bg-green-600", 잠실역_id, 노원_id, 10);

        // when
        ExtractableResponse<Response> result = postLine(일호선);

        // then
        assertThat(result.statusCode()).isEqualTo(BAD_REQUEST);
        assertThat(result.body().asString()).isEqualTo("중복되는 라인 정보가 존재합니다.");
    }

    @Test
    @DisplayName("노선 생성 - 실패(request 필수값 누락)")
    void create_실패_입력값이상() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);
        LineRequest 이름없는노선 =
                new LineRequest("", "bg-green-600", 잠실역_id, 잠실새내_id, 10);

        // when
        ExtractableResponse<Response> result = postLine(이름없는노선);

        // then
        assertThat(result.statusCode()).isEqualTo(BAD_REQUEST);
        assertThat(result.body().asString()).isEqualTo("필수값이 잘못 되었습니다.");
    }

    @Test
    @DisplayName("노선 생성 - 실패(등록되지 않는 역을 노선 종점역에 등록할 때)")
    void create_실패_없는역() {
        // given
        LineRequest 삼호선 =
                new LineRequest("3호선", "bg-green-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> result = postLine(삼호선);

        // then
        assertThat(result.statusCode()).isEqualTo(NOT_FOUND);
    }

    @Test
    @DisplayName("노선 생성 - 실패(상행선과 하행선 역이 같을 경우)")
    void create_실패_같은역() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        LineRequest 이호선 =
                new LineRequest("2호선", "bg-green-600", 잠실역_id, 잠실역_id, 10);

        // when
        ExtractableResponse<Response> result = postLine(이호선);

        // then
        assertThat(result.statusCode()).isEqualTo(BAD_REQUEST);
        assertThat(result.body().asString()).isEqualTo("상행,하행역이 같은 구간입니다.");
    }

    @Test
    @DisplayName("노선 조회 - 실패(해당 노선이 없을 경우)")
    void read_실패_없는노선() {
        // given & when
        ExtractableResponse<Response> result = get("/lines/" + Long.MAX_VALUE);

        //then
        assertThat(result.statusCode()).isEqualTo(NOT_FOUND);
    }

    @DisplayName("노선 조회 - 성공")
    @Test
    void read_성공() {
        // given
        Long 잠실역_id = postStationAndGetId(잠실역);
        Long 잠실새내_id = postStationAndGetId(잠실새내역);

        // when
        LineRequest 사호선 = new LineRequest("4호선", "bg-blue-600", 잠실역_id, 잠실새내_id, 10);
        Long lineId = postLineAndGetId(사호선);
        ExtractableResponse<Response> result = get("/lines/" + lineId);

        // then
        assertThat(result.statusCode()).isEqualTo(OK);
        assertThat(result.jsonPath().getString("stations[0].name")).isEqualTo("잠실역");
        assertThat(result.jsonPath().getString("stations[1].name")).isEqualTo("잠실새내역");
    }

}
