package wooteco.subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import wooteco.subway.controller.dto.station.StationRequest;
import wooteco.subway.controller.dto.station.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.acceptance.ResponseCreator.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private StationRequest 서울대입구 = new StationRequest("서울대입구");
    private StationRequest 신림 = new StationRequest("신림");
    private StationRequest 선릉 = new StationRequest("선릉");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        // when
        ExtractableResponse<Response> response = createPostStationResponse(선릉);
        StationResponse stationResponse = response.body().jsonPath().getObject(".", StationResponse.class);
        System.out.println(stationResponse.getId());
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank(),
                () -> assertThat(stationResponse.getName()).isEqualTo(선릉.getName()),
                () -> assertThat(stationResponse.getId()).isNotNull()
        );
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 생성시 예외가 발생한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        // when
        createPostStationResponse(서울대입구);
        ExtractableResponse<Response> response = createPostStationResponse(서울대입구);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> 서울대입구응답 = createPostStationResponse(서울대입구);
        ExtractableResponse<Response> 신림응답 = createPostStationResponse(신림);
        // when
        ExtractableResponse<Response> response = createGetStationResponse();
        List<StationResponse> stationResponses = response.body().jsonPath().getList(".", StationResponse.class);
        List<Long> 추가한Id = postIds(서울대입구응답, 신림응답);
        List<Long> 전체Id = responseIds(response);
        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(전체Id).containsAll(추가한Id),
                () -> assertThat(stationResponses.stream().map(it -> it.getName()).collect(Collectors.toList()))
                        .containsAll(List.of(서울대입구.getName(), 신림.getName()))
        );
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = createPostStationResponse(서울대입구);
        String id = createResponse.header("Location").split("/")[2];
        // when
        ExtractableResponse<Response> response = createDeleteStationResponseById(id);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("없는 지하철역을 제거한다.")
    @Test
    void deleteNonStation() {
        // given
        // when
        ExtractableResponse<Response> response = createDeleteStationResponseById("-1");
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
