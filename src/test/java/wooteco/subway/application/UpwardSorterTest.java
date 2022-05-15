package wooteco.subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wooteco.subway.dto.StationResponse;

class UpwardSorterTest {

    private StationResponse station1;
    private StationResponse station2;
    private StationResponse station3;
    private StationResponse station4;

    @BeforeEach
    void setUp() {
        station1 = new StationResponse(1L, "강남역");
        station2 = new StationResponse(2L, "선릉역");
        station3 = new StationResponse(3L, "역삼역");
        station4 = new StationResponse(4L, "잠실역");
    }

    @Test
    void upwardSort() {
        Map<StationResponse, StationResponse> graph = Map.of(station1, station2,
            station2, station3,
            station3, station4);
        UpwardSorter sorter = new UpwardSorter();

        assertThat(sorter.getSortedStations(graph))
            .containsExactly(station1, station2, station3, station4);
    }

}