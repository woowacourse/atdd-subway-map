package wooteco.subway.station.web;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.AcceptanceTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[API] 지하철역 관련 기능")
@Transactional
public class StationApiControllerTest extends AcceptanceTest {

    @DisplayName("생성 - 성공")
    @Test
    void create_성공() {
        // given
        StationRequest 잠실역 = new StationRequest("잠실역");

        // when
        ExtractableResponse<Response> result = postStation(잠실역);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(result.header("Location")).isNotBlank();
    }


    @DisplayName("생성 - 실패(기존에 존재하는 지하철역 이름으로 지하철역을 생성)")
    @Test
    void create_실패_중복이름() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        postStation(강남역);

        // when
        ExtractableResponse<Response> result = postStation(강남역);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("조회 - 성공")
    @Test
    void read_성공() {
        // given
        StationRequest 병점역 = new StationRequest("병점역");
        ExtractableResponse<Response> 병점역_생성 = postStation(병점역);

        StationRequest 역삼역 = new StationRequest("역삼역");
        ExtractableResponse<Response> 역삼역_생성 = postStation(역삼역);

        List<Long> expectedLineIds = Arrays.asList(병점역_생성, 역삼역_생성).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        // when
        ExtractableResponse<Response> result = 역_불러오기();
        List<Long> resultLineIds = result.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }


    @DisplayName("제거 - 성공")
    @Test
    void delete_성공() {
        // given
        StationRequest 강남역 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_생성 = postStation(강남역);

        // when
        String uri = 강남역_생성.header("Location");
        ExtractableResponse<Response> result = delete(uri);

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 역_불러오기() {
        return get("/stations");
    }
}
