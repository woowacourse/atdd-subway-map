package wooteco.subway.line;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.station.StationResponse;

class LineResponseTest {

    private static final long ID = 1L;
    private static final String NAME = "부산역";
    private static final String COLOR = "bg-red-600";

    @Test
    @DisplayName("노선 관련 답변 생성")
    void createLineResponse() {
        // given

        // when
        LineResponse lineResponse = new LineResponse();

        // then
        assertThat(lineResponse).isInstanceOf(LineResponse.class);
    }

    @Test
    @DisplayName("노선 관련 Id 확인 생성")
    void checkLineIdResponse() {
        //given
        LineResponse lineResponse = new LineResponse(ID, NAME, COLOR);

        //when
        Long lineId = lineResponse.getId();

        //then
        assertThat(lineId).isEqualTo(ID);
    }

    @Test
    @DisplayName("노선 관련 Name 확인 생성")
    void checkLineNameResponse() {
        //given
        LineResponse lineResponse = new LineResponse(ID, NAME, COLOR);

        //when
        String lineName = lineResponse.getName();

        //then
        assertThat(lineName).isEqualTo(NAME);
    }

    @Test
    @DisplayName("노선 관련 Color 확인 생성")
    void checkLineColorResponse() {
        //given
        LineResponse lineResponse = new LineResponse(ID, NAME, COLOR);

        //when
        String lineColor = lineResponse.getColor();

        //then
        assertThat(lineColor).isEqualTo(COLOR);
    }

    @Test
    @DisplayName("노선에 속해있는 역 반환")
    void checkStationsOfLineResponse() {
        // given
        List<StationResponse> givenStations = Arrays.asList(
            new StationResponse((long) 1, "건대입구역"),
            new StationResponse((long) 2, "군자역"),
            new StationResponse((long) 3, "면목역")
        );
        LineResponse lineResponse = new LineResponse(ID, NAME, COLOR, givenStations);

        //when
        List<StationResponse> stations = lineResponse.getStations();

        //then
        assertThat(givenStations.get(0).getId()).isEqualTo(stations.get(0).getId());
        assertThat(givenStations.get(0).getName()).isEqualTo(stations.get(0).getName());
        assertThat(givenStations.get(1).getId()).isEqualTo(stations.get(1).getId());
        assertThat(givenStations.get(1).getName()).isEqualTo(stations.get(1).getName());
        assertThat(givenStations.get(2).getId()).isEqualTo(stations.get(2).getId());
        assertThat(givenStations.get(2).getName()).isEqualTo(stations.get(2).getName());
    }
}