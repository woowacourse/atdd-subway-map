package wooteco.subway.admin.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/truncate.sql")
public class LineTest {
    @Test
    void getLineStations() {
        Line line = new Line(1L, "2호선", null, LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));

        List<Long> stationIds = line.getLineStationsId();

        assertThat(stationIds.size()).isEqualTo(2);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(1)).isEqualTo(2L);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void removeLineStation(Long stationId) {
        Line line = new Line(1L, "2호선", null, LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));

        line.removeLineStationById(stationId);

        assertThat(line.getStations()).hasSize(1);
    }
}
