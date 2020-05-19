package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineStationsTest {
    private LineStation lineStation1;
    private LineStation lineStation2;
    private LineStation lineStation3;
    private LineStations lineStations;

    @DisplayName("현재 3개의 역이 구간에 등록되어 있다.")
    @BeforeEach
    void setUp() {
        lineStation1 = new LineStation(1L, 1L, 10, 10);
        lineStation2 = new LineStation(2L, 1L, 10, 10);
        lineStation3 = new LineStation(3L, 2L, 10, 10);
        lineStations = LineStations.createEmpty();
        lineStations.add(lineStation1);
        lineStations.add(lineStation2);
        lineStations.add(lineStation3);
    }

    @DisplayName("빈 Linestations에 새로운 구간을 등록한다.")
    @Test
    void createFirstLineStationInfo() {
        LineStation lineStation = new LineStation(1L, 1L, 10, 10);
        LineStations lineStations = LineStations.createEmpty();
        lineStations.add(lineStation);
        assertThat(lineStations.getLineStationsIds()).containsExactly(1L);
    }

    @DisplayName("존재하는 LineStations에, 첫번째 역으로 다른 역을 추가한다.")
    @Test
    public void insertIntoFirstIndex() {
        final LineStation newLineStation = new LineStation(4L, 4L, 10, 10);
        lineStations.add(newLineStation);
        assertThat(lineStations.getLineStationsIds().get(0)).isEqualTo(4L);
        assertThat(lineStations.getLineStationsIds().get(1)).isEqualTo(1L);
    }

    @DisplayName("존재하는 LineStations에, 마지막 역으로 다른 역을 추가한다.")
    @Test
    public void insertIntoLastIndex() {
        final LineStation newLineStation = new LineStation(4L, 3L, 10, 10);
        lineStations.add(newLineStation);
        assertThat(lineStations.getLineStationsIds().get(3)).isEqualTo(4L);
        assertThat(lineStations.getLineStationsIds().get(2)).isEqualTo(3L);
    }

    @DisplayName("존재하는 LineStations에, 중간역으로 다른 역을 추가한다.")
    @Test
    public void insertIntoMiddleIndex() {
        final LineStation newLineStation = new LineStation(6L, 2L, 10, 10);
        lineStations.add(newLineStation);
        assertThat(lineStations.getLineStationsIds().get(2)).isEqualTo(6L);
        assertThat(lineStations.getLineStationsIds().get(1)).isEqualTo(2L);
    }

    @DisplayName("존재하는 LineStations에, 첫번째 역을 연속적으로 넣는다.")
    @Test
    public void insertIntoFirstIndexTwice() {
        final LineStation newLineStation = new LineStation(6L, 6L, 10, 10);
        final LineStation newLineStation2 = new LineStation(5L, 5L, 10, 10);
        lineStations.add(newLineStation);
        lineStations.add(newLineStation2);
        assertThat(lineStations.getLineStationsIds().get(0)).isEqualTo(5L);
        assertThat(lineStations.getLineStationsIds().get(1)).isEqualTo(6L);
    }

    @DisplayName("존재하는 LineStations에, 중간의 역을 연속적으로 넣는다.")
    @Test
    public void insertIntoMiddleIndexTwice() {
        final LineStation newLineStation = new LineStation(6L, 2L, 10, 10);
        final LineStation newLineStation2 = new LineStation(10L, 6L, 10, 10);
        lineStations.add(newLineStation);
        lineStations.add(newLineStation2);
        assertThat(lineStations.getLineStationsIds().get(1)).isEqualTo(2L);
        assertThat(lineStations.getLineStationsIds().get(2)).isEqualTo(6L);
        assertThat(lineStations.getLineStationsIds().get(3)).isEqualTo(10L);
    }

    @DisplayName("존재하는 LineStations에, 마지막 역을 연속적으로 넣는다.")
    @Test
    public void insertIntoLastIndexTwice() {
        final LineStation newLineStation = new LineStation(4L, 3L, 10, 10);
        final LineStation newLineStation2 = new LineStation(10L, 4L, 10, 10);
        lineStations.add(newLineStation);
        lineStations.add(newLineStation2);
        final List<Long> ids = lineStations.getLineStationsIds();
        assertThat(ids.get(ids.size() - 1)).isEqualTo(10L);
    }

    @DisplayName("정상적으로 삭제된다.")
    @Test
    public void remove() {
        lineStations.remove(1L);
        assertThat(lineStations.getLineStationsIds().get(0)).isEqualTo(2L);
        assertThat(lineStations.getLineStationsIds()).hasSize(2);
    }
}