package wooteco.subway.station;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.dto.StationRequest;

class StationRequestTest {

    @Test
    @DisplayName("역 관련 요청 생성")
    void createStationRequest() {
        // given

        // when
        StationRequest stationRequest = new StationRequest();

        // then
        assertThat(stationRequest).isInstanceOf(StationRequest.class);
    }

    @Test
    @DisplayName("역 이름과 함께 역 관련 요청 생성")
    void createStationRequestWithName() {
        // given
        String name = "정릉역";

        // when
        StationRequest stationRequest = new StationRequest(name);

        // then
        assertThat(stationRequest).isInstanceOf(StationRequest.class);
        assertThat(stationRequest.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("역 관련 요청에서 이름 가져오기")
    void getName() {
        // given
        StationRequest stationRequest1 = new StationRequest();

        String givenName2 = "정릉역";
        StationRequest stationRequest2 = new StationRequest(givenName2);

        // when
        String name1 = stationRequest1.getName();
        String name2 = stationRequest2.getName();

        // then
        assertThat(name1).isNull();
        assertThat(name2).isEqualTo(givenName2);
    }
}