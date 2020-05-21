package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

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
        line = new Line(1L, "2호선", "bg-green-700", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @Test
    void getLineStations() {
        List<Long> stationIds = line.makeLineStationsIds();

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }

    @Test
    void addLineStation() {
        line.addLineStation(new LineStation(null, 4L, 10, 10));
        List<Long> stationIds = line.makeLineStationsIds();
        assertThat(stationIds).size().isEqualTo(4);
    }

    @Test
    void addLineStationBetweenTwo() {
        line.addLineStation(new LineStation(1L, 4L, 10, 10));
        List<Long> stationIds = line.makeLineStationsIds();
        assertThat(stationIds).size().isEqualTo(4);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void removeLineStation(Long stationId) {
        line.removeLineStationById(stationId);

        assertThat(line.getStations().size()).isEqualTo(2);
    }
}
