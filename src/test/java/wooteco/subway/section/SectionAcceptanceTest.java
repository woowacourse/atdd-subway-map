package wooteco.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.AcceptanceTest;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.station.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
@Sql("classpath:test.sql")
public class SectionAcceptanceTest extends AcceptanceTest {

    private final long 강남역_id = 1L;
    private final long 잠실역_id = 2L;
    private final long 당산역_id = 3L;

    @BeforeEach
    void beforeEach() {
        StationRequest 강남역 = new StationRequest("강남역");
        StationRequest 잠실역 = new StationRequest("잠실역");
        StationRequest 당산역 = new StationRequest("당산역");

        postStation(강남역);
        postStation(잠실역);
        postStation(당산역);

        LineRequest 이호선 = new LineRequest("이호선", "green", 강남역_id, 잠실역_id, 5);
        postLine(이호선);
    }

    @Test
    @DisplayName("구간 추가 - 추가하려는 구간의 상행이 등록된 구간의 하행종점인 경우")
    void createSection() {
        // given
        SectionRequest 잠실에서당산 = new SectionRequest(잠실역_id, 당산역_id, 5);

        // when
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 잠실에서당산);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가 - 추가하려는 구간의 하행이 등록된 구간의 상행종점인 경우")
    void createSection2() {
        // given
        SectionRequest 당산에서강남 = new SectionRequest(당산역_id, 강남역_id, 5);

        // when
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 당산에서강남);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가 - 이미 등록된 노선인 경우 예외를 던진다.")
    void createSectionWhenAlreadyRegistered() {
        // given
        SectionRequest 강남에서잠실 = new SectionRequest(강남역_id, 잠실역_id, 5);

        // when
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 강남에서잠실);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가 - 구간에 등록되어 있지 않은 역을 추가하는 경우 예외를 던진다.")
    void createSectionWhen() {
        // given
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4
        StationRequest 신림역 = new StationRequest("신림역"); // id = 5
        SectionRequest 잠실에서당산 = new SectionRequest(잠실역_id, 당산역_id, 5);
        SectionRequest 왕십리에서신림 = new SectionRequest(4L, 5L, 2);

        // when
        postStation(왕십리역);
        postStation(신림역);
        postSection("/lines/1/sections", 잠실에서당산);

        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 왕십리에서신림);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가 - 존재하지 않는 역으로 등록 요청이 들어오면 예외 발생")
    void createSectionWhenNotExistStations() {
        // given
        SectionRequest 당산에서없는역 = new SectionRequest(당산역_id, 4L, 5);

        // when
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 당산에서없는역);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("구간 추가 - 상행역과 하행역이 동일한 경우 예외를 발생한다.")
    void testSameStationsSection() {
        // given
        SectionRequest 상행하행_동일 = new SectionRequest(강남역_id, 강남역_id, 5);

        // when
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 상행하행_동일);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가(중간) - 추가하려는 구간의 거리가 등록된 구간 리스트의 거리 이상인 경우")
    void createWhenNewSectionsDistanceLonger() {
        // given
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4

        SectionRequest 잠실에서왕십리 = new SectionRequest(잠실역_id, 4L, 5);
        SectionRequest 당산에서왕십리 = new SectionRequest(당산역_id, 4L, 6);

        // when
        postStation(왕십리역);
        postSection("/lines/1/sections", 잠실에서왕십리);
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 당산에서왕십리);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 추가(중간) - 추가하려는 구간의 하행이 등록된 구간 리스트의 하행에 있는 경우")
    void createSectionAppendBeforeDown() {
        // given
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4

        SectionRequest 잠실에서왕십리 = new SectionRequest(잠실역_id, 4L, 5);
        SectionRequest 당산에서왕십리 = new SectionRequest(당산역_id, 4L, 3);

        // when
        postStation(왕십리역);
        postSection("/lines/1/sections", 잠실에서왕십리);
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 당산에서왕십리);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가(중간) - 추가하려는 구간의 상행이 등록된 구간 리스트의 상행에 있는 경우")
    void createSectionAppendUp() {
        // given
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4

        SectionRequest 잠실에서왕십리 = new SectionRequest(잠실역_id, 4L, 5);
        SectionRequest 당산에서왕십리 = new SectionRequest(잠실역_id, 당산역_id, 3);

        // when
        postStation(왕십리역);
        postSection("/lines/1/sections", 잠실에서왕십리);
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 당산에서왕십리);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("구간 추가(중간) - 추가하려는 구간의 거리가 등록된 구간 리스트의 거리 이상인 경우")
    void createWhenNewSectionsDistanceLonger2() {
        // given
        StationRequest 왕십리역 = new StationRequest("왕십리역"); // id = 4

        SectionRequest 잠실에서왕십리 = new SectionRequest(잠실역_id, 4L, 5);
        SectionRequest 당산에서왕십리 = new SectionRequest(잠실역_id, 당산역_id, 5);

        // when
        postStation(왕십리역);
        postSection("/lines/1/sections", 잠실에서왕십리);
        ExtractableResponse<Response> 구간_생성_응답 = postSection("/lines/1/sections", 당산에서왕십리);

        // then
        assertThat(구간_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        // given
        SectionRequest 잠실에서당산 = new SectionRequest(잠실역_id, 당산역_id, 5);

        // when
        postSection("/lines/1/sections", 잠실에서당산);
        ExtractableResponse<Response> 구간_삭제_응답 = deleteResponseFrom("/lines/1/sections?stationId=2");

        // then
        assertThat(구간_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
