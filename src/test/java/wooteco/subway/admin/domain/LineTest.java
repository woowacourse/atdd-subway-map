package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        line = new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "test-color");
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
    }

    @Test
    void validateAddLineStation() {
        assertThatThrownBy(() -> {
            line.addLineStation(new LineStation(1L, 3L, 10, 10));
        }).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("라인에 역 등록은 시작역부터 순서대로 해주세요.");
    }

    @Test
    void getLineStations() {
        List<Long> stationIds = line.getSortedStationIds();

        assertThat(stationIds.size()).isEqualTo(2);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(1)).isEqualTo(2L);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void removeLineStation(Long stationId) {
        line.removeLineStationByStationId(stationId);

        assertThat(line.getLineStations()).hasSize(1);
    }
}
