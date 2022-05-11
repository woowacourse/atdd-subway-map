package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MetroManagerTest {

    @DisplayName("하나의 구간으로 이루어지고, 같은 상행 역을 추가한다.")
    @Test
    void add_same_with_upStation_one_section() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L)));
        metroManager.add_right(new Section(1L, 3L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(3L);
        Adjacency result3 = metroManager.getAdjacency(2L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(-1L, 0L), new StationInfo(3L, 3L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 3L), new StationInfo(2L, 7L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(3L, 7L), new StationInfo(-1L, 0L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(2L));
    }

    @DisplayName("두개의 구간으로 이루어지고, 같은 상행 역을 추가한다.")
    @Test
    void add_same_with_upStation_two_section() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L)));
        metroManager.add_right(new Section(2L, 4L, 3L));
        Adjacency result1 = metroManager.getAdjacency(2L);
        Adjacency result2 = metroManager.getAdjacency(4L);
        Adjacency result3 = metroManager.getAdjacency(3L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(1L, 10L), new StationInfo(4L, 3L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(2L, 3L), new StationInfo(3L, 7L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(4L, 7L), new StationInfo(-1L, 0L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(3L));
    }

    @DisplayName("새로운 하행 종점을 추가한다.")
    @Test
    void add_new_down_station_end() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L)));
        metroManager.add_right(new Section(2L, 3L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(2L);
        Adjacency result3 = metroManager.getAdjacency(3L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(-1L, 0L), new StationInfo(2L, 10L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 10L), new StationInfo(3L, 3L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(2L, 3L), new StationInfo(-1L, 0L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(3L));
    }

    @DisplayName("새로운 상행 종점을 추가한다.")
    @Test
    void add_new_up_station_end() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L)));
        metroManager.add_left(new Section(3L, 1L, 3L));
        Adjacency result1 = metroManager.getAdjacency(3L);
        Adjacency result2 = metroManager.getAdjacency(1L);
        Adjacency result3 = metroManager.getAdjacency(2L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(-1L, 0L), new StationInfo(1L, 3L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(3L, 3L), new StationInfo(2L, 10L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(1L, 10L), new StationInfo(-1L, 0L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(3L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(2L));
    }

    @DisplayName("하나의 구간으로 이루어지고, 같은 하행 역을 추가한다.")
    @Test
    void add_same_with_downStation_one_section() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L)));
        metroManager.add_left(new Section(3L, 2L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(3L);
        Adjacency result3 = metroManager.getAdjacency(2L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(-1L, 0L), new StationInfo(3L, 7L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 7L), new StationInfo(2L, 3L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(3L, 3L), new StationInfo(-1L, 0L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(2L));
    }

    @DisplayName("두개의 구간으로 이루어지고, 같은 하행 역을 추가한다.")
    @Test
    void add_same_with_downStation_two_section() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L)));
        metroManager.add_left(new Section(4L, 2L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(4L);
        Adjacency result3 = metroManager.getAdjacency(2L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(-1L, 0L), new StationInfo(4L, 7L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 7L), new StationInfo(2L, 3L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(4L, 3L), new StationInfo(3L, 10L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(3L));
    }

    @DisplayName("크기가 1인 사이클을 만드는 구간을 추가한다.")
    @Test
    void add_cycle_size_one() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L)));
        metroManager.add_cycle(new Section(2L, 1L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(2L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(2L, 3L), new StationInfo(2L, 10L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 10L), new StationInfo(1L, 3L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(-1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(-1L));
    }

    @DisplayName("크기가 2인 사이클을 만드는 구간을 추가한다.")
    @Test
    void add_cycle_size_two() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L)));
        metroManager.add_cycle(new Section(3L, 1L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(3L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(3L, 3L), new StationInfo(2L, 10L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(2L, 10L), new StationInfo(1L, 3L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(-1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(-1L));
    }

    @DisplayName("사이클이 있는 상태에서 일반 구간에 같은 상행 역을 추가한다.")
    @Test
    void add_up_station_when_cycled() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L), new Section(3L, 1L, 3L)));
        metroManager.add_right(new Section(1L, 4L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(4L);
        Adjacency result3 = metroManager.getAdjacency(2L);
        Adjacency result4 = metroManager.getAdjacency(3L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(3L, 3L), new StationInfo(4L, 3L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 3L), new StationInfo(2L, 7L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(4L, 7L), new StationInfo(3L, 10L))),
                () -> assertThat(result4).isEqualTo(new Adjacency(new StationInfo(2L, 10L), new StationInfo(1L, 3L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(-1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(-1L));
    }

    @DisplayName("사이클이 있는 상태에서 일반 구간에 같은 하행 역을 추가한다.")
    @Test
    void add_down_station_when_cycled() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L), new Section(3L, 1L, 3L)));
        metroManager.add_left(new Section(4L, 3L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(2L);
        Adjacency result3 = metroManager.getAdjacency(3L);
        Adjacency result4 = metroManager.getAdjacency(4L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(3L, 3L), new StationInfo(2L, 10L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 10L), new StationInfo(4L, 7L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(4L, 3L), new StationInfo(1L, 3L))),
                () -> assertThat(result4).isEqualTo(new Adjacency(new StationInfo(2L, 7L), new StationInfo(3L, 3L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(-1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(-1L));
    }

    @DisplayName("사이클이 있는 상태에서 사이클을 만드는 구간에 같은 상행 역을 추가한다.")
    @Test
    void add_up_station_when_cycle_edge() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L), new Section(3L, 1L, 10L)));
        metroManager.add_right(new Section(3L, 4L, 3L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(2L);
        Adjacency result3 = metroManager.getAdjacency(3L);
        Adjacency result4 = metroManager.getAdjacency(4L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(4L, 7L), new StationInfo(2L, 10L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 10L), new StationInfo(3L, 10L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(2L, 10L), new StationInfo(4L, 3L))),
                () -> assertThat(result4).isEqualTo(new Adjacency(new StationInfo(3L, 3L), new StationInfo(1L, 7L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(-1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(-1L));
    }

    @DisplayName("사이클이 있는 상태에서 사이클을 만드는 구간에 같은 하행 역을 추가한다.")
    @Test
    void add_down_station_when_cycle_edge() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L), new Section(3L, 1L, 10L)));
        metroManager.add_left(new Section(4L, 1L, 7L));
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(2L);
        Adjacency result3 = metroManager.getAdjacency(3L);
        Adjacency result4 = metroManager.getAdjacency(4L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(4L, 7L), new StationInfo(2L, 10L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 10L), new StationInfo(3L, 10L))),
                () -> assertThat(result3).isEqualTo(new Adjacency(new StationInfo(2L, 10L), new StationInfo(4L, 3L))),
                () -> assertThat(result4).isEqualTo(new Adjacency(new StationInfo(3L, 3L), new StationInfo(1L, 7L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(-1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(-1L));
    }

    @DisplayName("순환 구간을 삭제하면 직선 구간이 된다.")
    @Test
    void delete_cycle() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L), new Section(3L, 1L, 10L)));
        metroManager.delete_cycle(1L);
        Adjacency result1 = metroManager.getAdjacency(2L);
        Adjacency result2 = metroManager.getAdjacency(3L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(-1L, 0L), new StationInfo(3L, 10L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(2L, 10L), new StationInfo(-1L, 0L))),
                () -> assertThat(metroManager.isCycle()).isFalse(),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(2L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(3L));
    }

    @DisplayName("종점이 아닌 역을 삭제하면 직선 구간이 된다.")
    @Test
    void delete() {
        MetroManager metroManager = new MetroManager(List.of(new Section(1L, 2L, 10L), new Section(2L, 3L, 10L)));
        metroManager.delete(2L);
        Adjacency result1 = metroManager.getAdjacency(1L);
        Adjacency result2 = metroManager.getAdjacency(3L);
        assertAll(() -> assertThat(result1).isEqualTo(new Adjacency(new StationInfo(-1L, 0L), new StationInfo(3L, 10L))),
                () -> assertThat(result2).isEqualTo(new Adjacency(new StationInfo(1L, 10L), new StationInfo(-1L, 0L))),
                () -> assertThat(metroManager.findUpStationEnd()).isEqualTo(1L),
                () -> assertThat(metroManager.findDownStationEnd()).isEqualTo(3L));
    }
}
