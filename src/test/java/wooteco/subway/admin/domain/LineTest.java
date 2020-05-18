package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class LineTest {
    private Line line = new Line(1L, "2호선", "red", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
    private LineStation lineStation0to1;
    private LineStation lineStation1to2;
    private LineStation lineStation2to3;
    private LineStation lineStation1to3;
    private LineStation lineStation3to4;

    @BeforeEach
    void setUp() {
        lineStation0to1 = new LineStation(null, 1L, 10, 10);
        lineStation1to2 = new LineStation(1L, 2L, 10, 10);
        lineStation2to3 = new LineStation(2L, 3L, 10, 10);
        lineStation1to3 = new LineStation(1L, 3L, 10, 10);
        lineStation3to4 = new LineStation(3L, 4L, 10, 10);
    }

    @DisplayName("빈 노선에 구간을 추가했을 때 제대로 추가가 되는지 확인")
    @Test
    void addLineStationInFirstNode() {
        line.addLineStation(lineStation0to1);
        Set<LineStation> stations = line.getStations();
        assertThat(stations.size()).isEqualTo(1);
        assertThat(stations).contains(lineStation0to1);
    }

    @DisplayName("노선의 마지막 구간을 추가했을 때 제대로 추가가 되는지 확인")
    @Test
    void addLineStationInLastNode() {
        line.addLineStation(lineStation0to1);
        line.addLineStation(lineStation1to2);
        Set<LineStation> stations = line.getStations();
        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations).contains(lineStation0to1, lineStation1to2);
    }

    @DisplayName("구간 사이에 새로운 구간이 추가될 때 제대로 추가가 되는지 확인")
    @Test
    void addLineStationBetweenNodes() {
        line.addLineStation(lineStation0to1);
        line.addLineStation(lineStation1to2);
        line.addLineStation(lineStation1to3);
        Set<LineStation> stations = line.getStations();
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations).contains(lineStation0to1, lineStation1to2, lineStation1to3);
    }

    /*
     * 1. 연결되어 있지 않은 노선 (0-1-2 + 3-4)
     * 2. 아무것도 없을 때 출발역 지정이 안될 때? (1-2)
     */

    @DisplayName("연결되어 있지 않은 노선이 추가될 때")
    @Test
    void unConnectedLineStation() {
        line.addLineStation(lineStation3to4);
        // assertThat()
    }

    @DisplayName("역을 지웠을 때 ")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void removeLineStation(Long stationId) {
        line.addLineStation(lineStation0to1);
        line.addLineStation(lineStation1to2);
        line.addLineStation(lineStation2to3);
        line.removeLineStationById(stationId);
        assertThat(line.getStations()).hasSize(2);
    }

    @DisplayName("한 노선에 있는 역들의 아이디들을 순서대로 반환하는 메서드가 제대로 동작하는지 확인")
    @Test
    void findLineStationsId() {
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));

        List<Long> stationIds = line.findLineStationsId();
        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }
}
