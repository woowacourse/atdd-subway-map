package wooteco.subway.admin.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/truncate.sql")
public class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "bg-red-500", "2호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        line.addLineStation(new LineStation(1L, 2L, 3L, 10, 10));
        line.addLineStation(new LineStation(1L, 3L, 4L, 10, 10));
        line.addLineStation(new LineStation(1L, 4L, 5L, 10, 10));
    }

    @Test
    void getLineStations() {
        List<Long> stationIds = line.getLineStationsId();

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(3L);
        assertThat(stationIds.get(1)).isEqualTo(4L);
        assertThat(stationIds.get(2)).isEqualTo(5L);
    }

    @ParameterizedTest
    @ValueSource(longs = {3L, 4L, 5L})
    void removeLineStation(Long stationId) {
        line.removeLineStationById(stationId);

        assertThat(line.getStations()).hasSize(2);
    }
}
