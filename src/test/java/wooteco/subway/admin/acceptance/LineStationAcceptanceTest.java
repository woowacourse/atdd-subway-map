package wooteco.subway.admin.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineWithOrderedStationsResponse;
import wooteco.subway.admin.dto.StationResponse;

public class LineStationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선에서 지하철역 추가 / 제외")
    @Test
    void manageLineStation() {
        // given
        createStation("마장역");
        createStation("왕십리역");
        createStation("행당역");
        createStation("몽촌토성역");
        createLine("5호선");
        List<StationResponse> stations = getStations();
        List<LineResponse> lines = getLines();

        //when
        createLineStation(lines.get(0).getId(), stations.get(0).getId(), null);
        createLineStation(lines.get(0).getId(), stations.get(1).getId(), stations.get(0).getId());
        createLineStation(lines.get(0).getId(), stations.get(2).getId(), stations.get(1).getId());
        LineWithOrderedStationsResponse lineWithStations = getLineWithStations(lines.get(0).getId());
        //then
        assertThat(lineWithStations.getOrderedStations().size()).isEqualTo(3);

        //when
        createLineStation(lines.get(0).getId(), stations.get(3).getId(), stations.get(2).getId());
        //then
        List<Station> orderedStations = getLineWithStations(lines.get(0).getId()).getOrderedStations();
        Station station = orderedStations.get(orderedStations.size() - 1);

        //when
        deleteLineStation(lines.get(0).getId(),orderedStations.get(orderedStations.size() - 1).getId());
        //then
        assertThat(lineWithStations.getOrderedStations().size()).isEqualTo(3);

        orderedStations = getLineWithStations(lines.get(0).getId()).getOrderedStations();
        assertThat(orderedStations.stream()
                .anyMatch(orderedStation -> orderedStation.getName().equals("몽촌토성역"))
        ).isFalse();
    }


}
