package wooteco.subway.admin.domain;

import static java.util.stream.Collectors.*;
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
        line = new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-orange-500");
        line.addLineStation(new LineStation(null, 1L));
        line.addLineStation(new LineStation(1L, 2L));
        line.addLineStation(new LineStation(2L, 3L));
    }

    @Test
    void getLineStations() {
        List<Long> stationIds = getStationsIds(line);

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void removeLineStation(Long stationId) {
        line.removeLineStationById(stationId);

        assertThat(line.getStations()).hasSize(2);
    }

    private List<Long> getStationsIds(Line line) {
        return line.getStations().stream()
            .map(LineStation::getStationId)
            .collect(toList());
    }
}
