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
        line = new Line(1L, "2호선", LocalTime.of(5, 30), LocalTime.of(22, 30), 5);
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @DisplayName("저장된 역을 모두 불러오는지 테스트")
    @Test
    void getLineStations() {
        List<Long> stationIds = line.findLineStationsId();

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }

    @DisplayName("노선에 포함되지 않은 역을 이용하여 구간을 추가할 때 예외 처리 테스트")
    @Test
    void addLineStationWithInvalidStationId() {
        assertThatThrownBy(() -> {
            line.addLineStation(new LineStation(4L, 5L, 10, 10));
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("아이디에 해당되는 역을 삭제하는지 테스트")
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void removeLineStation(Long stationId) {
        line.removeLineStationById(stationId);

        assertThat(line.getLineStations()).hasSize(2);
    }

    @DisplayName("노선에 포함되지 않은 역을 이용하여 구간을 삭제할 때 예외 처리 테스트")
    @Test
    void removeLineStationWithInvalidStationId() {
        assertThatThrownBy(() -> {
            line.removeLineStationById(4L);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선 정보 업데이트 테스트")
    @Test
    void updateTest() {
        Line updateLine = new Line("3호선", LocalTime.of(6, 40), LocalTime.of(23, 50), 8);
        line.update(updateLine);

        assertThat(line.getName()).isEqualTo("3호선");
        assertThat(line.getStartTime()).isEqualTo(LocalTime.of(6, 40));
        assertThat(line.getEndTime()).isEqualTo(LocalTime.of(23, 50));
        assertThat(line.getIntervalTime()).isEqualTo(8);
    }

    @DisplayName("노선에 포함된 구간의 역 아이디 반환 테스트")
    @Test
    void findLineStationsIdTest() {
        List<Long> lineStationsIds = line.findLineStationsId();

        assertThat(lineStationsIds.size()).isEqualTo(3);
        assertThat(lineStationsIds).contains(1L, 2L, 3L);
    }
}
