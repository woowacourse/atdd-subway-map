package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionTest {

    @DisplayName("상행 종점과 하행 종점이 같으면 예외가 발생한다")
    @Test
    void sameUpStationAndDownStation() {
        Station station1 = Station.of("1");
        Line line = new Line.Builder("2호선", "초록색")
                .build();
        ;

        assertThatThrownBy(() -> new Section.Builder(line, station1, station1, 3)
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("거리가 양의 정수가 아니면 예외가 발생한다")
    @Test
    void unvalidDistanceValue() {
        Station station1 = Station.of("1");
        Station station2 = Station.of("2");
        Line line = new Line.Builder("2호선", "초록색")
                .build();
        ;

        assertThatThrownBy(() -> new Section.Builder(line, station1, station2, -3)
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
