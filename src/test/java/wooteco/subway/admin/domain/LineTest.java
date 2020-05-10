package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class LineTest {

    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", "임시 컬러", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @Test
    void getLineStations() {
        List<Long> stationIds = line.getLineStationsId();

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void removeLineStation(Long stationId) {
        line.removeLineStationById(stationId);

        assertThat(line.getLineStations()).hasSize(2);
    }

    @Test
    void update() {
        Line line = new Line(1L, "2호선", "임시 컬러", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        Line updateLine = new Line(1L, "3호선", "임시 컬러2", LocalTime.of(6, 30), LocalTime.of(6, 30),
            10);
        line.update(updateLine);

        assertThat(line.getName()).isEqualTo(updateLine.getName());
        assertThat(line.getColor()).isEqualTo(updateLine.getColor());
        assertThat(line.getStartTime()).isEqualTo(updateLine.getStartTime());
        assertThat(line.getEndTime()).isEqualTo(updateLine.getEndTime());
        assertThat(line.getIntervalTime()).isEqualTo(updateLine.getIntervalTime());
    }

    @Test
    void addLineStation_sequentialAddStation() {
        line = new Line(2L, "3호선", "임시 컬러", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        LineStation firstLineStation = new LineStation(null, 1L, 10, 10);
        LineStation secondLineStation = new LineStation(1L, 2L, 10, 10);
        LineStation finalLineStation = new LineStation(2L, 3L, 10, 10);
        line.addLineStation(firstLineStation);
        line.addLineStation(secondLineStation);
        line.addLineStation(finalLineStation);
        List<LineStation> lineStations = line.getLineStations();

        assertThat(lineStations.size()).isEqualTo(3);
        assertThat(lineStations.get(0)).isEqualTo(firstLineStation);
        assertThat(lineStations.get(1)).isEqualTo(secondLineStation);
        assertThat(lineStations.get(2)).isEqualTo(finalLineStation);
    }

    @Test
    void addLineStation_reverseSequentialAddStation() {
        line = new Line(2L, "3호선", "임시 컬러", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        LineStation firstLineStation = new LineStation(null, 1L, 10, 10);
        LineStation overwriteFirstLineStation = new LineStation(null, 2L, 10, 10);
        line.addLineStation(firstLineStation);
        line.addLineStation(overwriteFirstLineStation);
        List<LineStation> lineStations = line.getLineStations();

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.get(0)).isEqualTo(overwriteFirstLineStation);
        assertThat(lineStations.get(1).getPreStationId())
            .isEqualTo(overwriteFirstLineStation.getStationId());
        assertThat(lineStations.get(1).getStationId()).isEqualTo(firstLineStation.getStationId());
        assertThat(lineStations.get(1).getDistance()).isEqualTo(firstLineStation.getDistance());
        assertThat(lineStations.get(1).getDuration()).isEqualTo(firstLineStation.getDuration());
    }

    @Test
    void addLineStation_randomSequentialAddStation() {
        line = new Line(2L, "3호선", "임시 컬러", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        LineStation firstLineStation = new LineStation(null, 1L, 10, 10);
        LineStation secondLineStation = new LineStation(1L, 2L, 10, 10);
        LineStation overwriteSecondLineStation = new LineStation(1L, 3L, 10, 10);
        line.addLineStation(firstLineStation);
        line.addLineStation(secondLineStation);
        line.addLineStation(overwriteSecondLineStation);
        List<LineStation> lineStations = line.getLineStations();

        assertThat(lineStations.size()).isEqualTo(3);
        assertThat(lineStations.get(0)).isEqualTo(firstLineStation);
        assertThat(lineStations.get(1)).isEqualTo(overwriteSecondLineStation);
        assertThat(lineStations.get(2).getPreStationId())
            .isEqualTo(overwriteSecondLineStation.getStationId());
        assertThat(lineStations.get(2).getStationId()).isEqualTo(secondLineStation.getStationId());
        assertThat(lineStations.get(2).getDistance()).isEqualTo(secondLineStation.getDistance());
        assertThat(lineStations.get(2).getDuration()).isEqualTo(secondLineStation.getDuration());
    }

    @Test
    void removeLineStationById_first() {
        line = new Line(2L, "3호선", "임시 컬러", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        LineStation firstLineStation = new LineStation(null, 1L, 10, 10);
        LineStation secondLineStation = new LineStation(1L, 2L, 10, 10);
        LineStation finalLineStation = new LineStation(2L, 3L, 10, 10);
        line.addLineStation(firstLineStation);
        line.addLineStation(secondLineStation);
        line.addLineStation(finalLineStation);
        line.removeLineStationById(1L);
        List<LineStation> lineStations = line.getLineStations();

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.get(0).getPreStationId()).isEqualTo(null);
        assertThat(lineStations.get(0).getStationId()).isEqualTo(secondLineStation.getStationId());
        assertThat(lineStations.get(0).getDistance()).isEqualTo(secondLineStation.getDistance());
        assertThat(lineStations.get(0).getDuration()).isEqualTo(secondLineStation.getDuration());
        assertThat(lineStations.get(1)).isEqualTo(finalLineStation);
    }

    @Test
    void removeLineStationById_middle() {
        line = new Line(2L, "3호선", "임시 컬러", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        LineStation firstLineStation = new LineStation(null, 1L, 10, 10);
        LineStation secondLineStation = new LineStation(1L, 2L, 10, 10);
        LineStation finalLineStation = new LineStation(2L, 3L, 10, 10);
        line.addLineStation(firstLineStation);
        line.addLineStation(secondLineStation);
        line.addLineStation(finalLineStation);
        line.removeLineStationById(2L);
        List<LineStation> lineStations = line.getLineStations();

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.get(0)).isEqualTo(firstLineStation);
        assertThat(lineStations.get(1).getPreStationId())
            .isEqualTo(firstLineStation.getStationId());
        assertThat(lineStations.get(1).getStationId()).isEqualTo(finalLineStation.getStationId());
        assertThat(lineStations.get(1).getDistance()).isEqualTo(finalLineStation.getDistance());
        assertThat(lineStations.get(1).getDuration()).isEqualTo(finalLineStation.getDuration());
    }

    @Test
    void removeLineStationById_final() {
        line = new Line(2L, "3호선", "임시 컬러", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        LineStation firstLineStation = new LineStation(null, 1L, 10, 10);
        LineStation secondLineStation = new LineStation(1L, 2L, 10, 10);
        LineStation finalLineStation = new LineStation(2L, 3L, 10, 10);
        line.addLineStation(firstLineStation);
        line.addLineStation(secondLineStation);
        line.addLineStation(finalLineStation);
        line.removeLineStationById(3L);
        List<LineStation> lineStations = line.getLineStations();

        assertThat(lineStations.size()).isEqualTo(2);
        assertThat(lineStations.get(0)).isEqualTo(firstLineStation);
        assertThat(lineStations.get(1)).isEqualTo(secondLineStation);
    }
}
