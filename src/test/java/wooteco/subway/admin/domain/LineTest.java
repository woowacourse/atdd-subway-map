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
        line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-gray-500");
        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @Test
    void create_Fail_When_NameEmpty() {
        assertThatThrownBy(() -> new Station(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("name은 빈 값이 올 수 없습니다.");
    }

    @Test
    void create_Fail_When_NameContainsSpace() {
        assertThatThrownBy(() -> new Station("지 하철"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("name은 공백이 포함 될 수 없습니다.");
    }

    @Test
    void create_Fail_When_IntervalUnderZero() {
        assertThatThrownBy(() ->
            new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 0, "bg-blue-500"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Line.inttervalTime은 0이하일 수 없습니다.");
        assertThatThrownBy(() ->
            new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), -1, "bg-blue-500"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Line.inttervalTime은 0이하일 수 없습니다.");
    }

    @Test
    void create_Fail_When_BgColorFormatError() {
        assertThatThrownBy(() ->
            new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 3, "dd"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Line.bgColor의 형식이 아닙니다. (bg-color-no)");
        assertThatThrownBy(() ->
            new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 3, "bg-3-500"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Line.bgColor의 형식이 아닙니다. (bg-color-no)");
        assertThatThrownBy(() ->
            new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 3, "bg-blue-blue"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Line.bgColor의 형식이 아닙니다. (bg-color-no)");
    }

    @Test
    void getLineStations() {
        List<Long> stationIds = line.getStationIds();

        assertThat(stationIds.size()).isEqualTo(3);
        assertThat(stationIds.get(0)).isEqualTo(1L);
        assertThat(stationIds.get(2)).isEqualTo(3L);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void removeLineStation(Long stationId) {
        line.removeLineStationById(stationId);

        assertThat(line.getStations().getStations()).hasSize(2);
    }
}
