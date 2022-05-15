package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("상행역과 하행역, 거리로 구간을 생성한다.")
    @Test
    void create() {
        // given
        Line line = new Line("우테코노선", "황금색");
        Station 수달역 = new Station("수달역");
        Station 토미역 = new Station("토미역");
        int distance = 100;

        // then
        assertThatNoException().isThrownBy(() -> new Section(line, 수달역, 토미역, distance));
    }

    @DisplayName("거리가 음수인 구간을 생성한다")
    @Test
    void createFalseDistance() {
        // given
        Line line = new Line("우테코노선", "황금색");
        Station 수달역 = new Station("수달역");
        Station 토미역 = new Station("토미역");
        int distance = -1;

        // then
        assertThatThrownBy(() -> new Section(line, 수달역, 토미역, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Section.DISTANCE_NEGATIVE_ERROR_MESSAGE);
    }

    @DisplayName("기존에 있던 구간에서 상행역을 기준으로 구간이 추가될 때, 더 큰 거리가 들어오면 예외가 발생된다.")
    @Test
    void splitFromUpStationFalse() {
        // given
        Line line = new Line("우테코노선", "황금색");
        Station 수달역 = new Station("수달역");
        Station 토미역 = new Station("토미역");
        Station 브리역 = new Station("브리역");
        int distance = 10;

        Section section = new Section(line, 수달역, 토미역, distance);
        Section target = new Section(line, 수달역, 브리역, distance + 1);

        assertThatThrownBy(() -> section.splitFromUpStation(target))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Section.DISTANCE_OVER_ERROR_MESSAGE);
    }

    @DisplayName("기존에 있던 구간에서 하행역을 기준으로 구간이 추가될 때, 더 큰 거리가 들어오면 예외가 발생된다.")
    @Test
    void splitFromDownStationFalse() {
        // given
        Line line = new Line("우테코노선", "황금색");
        Station 수달역 = new Station("수달역");
        Station 토미역 = new Station("토미역");
        Station 브리역 = new Station("브리역");
        int distance = 10;

        Section section = new Section(line, 수달역, 토미역, distance);
        Section target = new Section(line, 토미역, 브리역, distance + 1);

        assertThatThrownBy(() -> section.splitFromDownStation(target))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(Section.DISTANCE_OVER_ERROR_MESSAGE);
    }

    @DisplayName("구간의 상행역과 일치하는 역인지 확인한다.")
    @Test
    void isSameUpStation() {
        // given
        Line line = new Line("우테코노선", "황금색");
        Station 수달역 = new Station("수달역");
        Station 토미역 = new Station("토미역");
        Station 브리역 = new Station("브리역");
        int distance = 10;
        // when
        Section section = new Section(line, 수달역, 토미역, distance);
        // then
        assertTrue(section.hasSameUpStation(수달역));
    }

    @DisplayName("구간의 하행역과 일치하는 역인지 확인한다.")
    @Test
    void isSameDownStation() {
        // given
        Line line = new Line("우테코노선", "황금색");
        Station 수달역 = new Station("수달역");
        Station 토미역 = new Station("토미역");
        Station 브리역 = new Station("브리역");
        int distance = 10;
        // when
        Section section = new Section(line, 수달역, 토미역, distance);
        // then
        assertTrue(section.hasSameDownStation(토미역));
    }
}

