package wooteco.subway.station;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.controller.dto.response.StationResponse;

class StationResponseTest {

    private static final long ID = 1L;
    private static final String NAME = "부산역";

    @Test
    @DisplayName("역 관련 답변 생성")
    void createStationResponse() {
        // given

        // when
        StationResponse stationResponse = new StationResponse();

        // then
        assertThat(stationResponse).isInstanceOf(StationResponse.class);
    }

    @Test
    @DisplayName("역 관련 Id 확인 생성")
    void checkStationIdResponse() {
        // given
        StationResponse stationResponse = new StationResponse(ID, NAME);

        // when
        Long stationId = stationResponse.getId();

        // then
        assertThat(stationId).isEqualTo(ID);
    }

    @Test
    @DisplayName("역 관련 Name 확인 생성")
    void checkStationNameResponse() {
        // given
        StationResponse stationResponse = new StationResponse(ID, NAME);

        // when
        Long stationId = stationResponse.getId();
        String stationName = stationResponse.getName();

        // then
        assertThat(stationName).isEqualTo(NAME);
    }
}