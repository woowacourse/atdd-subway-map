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
        // given : line 테이블에는 '5호선' 이라는 노선이,
        // station 테이블에는 다음 네개의 역이 저장되어있다.
        createStation("마장역");
        createStation("왕십리역");
        createStation("행당역");
        createStation("몽촌토성역");
        createLine("5호선");

        List<StationResponse> stations = getStations();
        List<LineResponse> lines = getLines();

        // when : 5호선에 마장역 -> 왕십리역 -> 행당역 순서로 lineStation 을 등록한다.
        createLineStation(lines.get(0).getId(), stations.get(0).getId(), null);
        createLineStation(lines.get(0).getId(), stations.get(1).getId(), stations.get(0).getId());
        createLineStation(lines.get(0).getId(), stations.get(2).getId(), stations.get(1).getId());

        // then : 5호선에 대하여 세개의 역이 정상적으로 등록되었는가?
        LineWithOrderedStationsResponse lineWithStations = getLineWithStations(lines.get(0).getId());
        assertThat(lineWithStations.getOrderedStations().size()).isEqualTo(3);

        // when : 5호선의 행당역(기존의 마지막 역) 다음에 몽촌토성역을 등록한다.
        createLineStation(lines.get(0).getId(), stations.get(3).getId(), stations.get(2).getId());
        // then : 마지막 역이 몽촌토성역인가?
        List<Station> orderedStations = getLineWithStations(lines.get(0).getId()).getOrderedStations();
        assertThat(orderedStations.get(orderedStations.size() - 1).getName()).isEqualTo("몽촌토성역");

        // when : 5호선 마지막 역(= 몽촌토성역)을 5호선에서 삭제한다.
        deleteLineStation(lines.get(0).getId(),orderedStations.get(orderedStations.size() - 1).getId());
        // then (1) : 5호선의 역 갯수가 세개인가? (4개에서 3개로 줄었는가?)
        assertThat(lineWithStations.getOrderedStations().size()).isEqualTo(3);
        // then (2) : 5호선에 몽촌토성역이 정상적으로 삭제되었는가?
        orderedStations = getLineWithStations(lines.get(0).getId()).getOrderedStations();
        assertThat(orderedStations.stream()
                .anyMatch(orderedStation -> orderedStation.getName().equals("몽촌토성역"))
        ).isFalse();
    }


}
