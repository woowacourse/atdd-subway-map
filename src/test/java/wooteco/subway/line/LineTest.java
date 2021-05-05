package wooteco.subway.line;

import org.junit.jupiter.api.Test;
import wooteco.subway.station.Station;

import static org.assertj.core.api.Assertions.*;

public class LineTest {

    @Test
    void lineCreateTest() {
        Line line = new Line("2호선", new Station("강남역"), new Station("건대역"));

        assertThat(line).isInstanceOf(Line.class);
    }

    @Test
    void duplicatedStationException() {
        assertThatThrownBy(() -> new Line("2호선", new Station("강남역"), new Station("강남역")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
