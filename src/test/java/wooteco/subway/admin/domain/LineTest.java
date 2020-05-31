package wooteco.subway.admin.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class LineTest {
    private Line line;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "white");
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @DisplayName("LineStation 을 추가하는 테스트")
    @Test
    void addLineStation() {
        line.addLineStation(new LineStation(3L, 4L, 10, 10));

        assertThat(line.getLineStationIds()).hasSize(4);
    }

    @DisplayName("LineStation 을 가져오는 테스트")
    @Test
    void getLineStations() {
        List<Long> stationIds = line.getLineStationIds();

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }

    @DisplayName("시작역이 없어서 LineStation 을 가져오지 못하는 테스트")
    @Test
    void exceptionGetLineStations() {
        Line Line1 = line = new Line("1호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "white");
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));

        assertThatThrownBy(Line1::getLineStationIds).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("LineStation 을 PreStationId 로 찾을 수 없습니다.");
    }

    @DisplayName("LineStation 을 제거 하는 테스트")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void removeLineStation(Long stationId) {
        line.removeLineStationById(stationId);

        assertThat(line.getStations()).hasSize(2);
    }

    @DisplayName("존재하지 않는 LineStation 을 제거 하는 테스트")
    @Test
    void exceptionRemoveLineStation() {
        assertThatThrownBy(() -> line.removeLineStationById(9L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("LineStation 을 StationId로 찾을 수 없습니다.");
    }
}

