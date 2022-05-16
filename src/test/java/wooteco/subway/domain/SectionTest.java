package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static wooteco.subway.domain.LineFixtures.*;
import static wooteco.subway.domain.StationFixtures.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("상행역과 하행역 중 특정 역이 있는지 확인한다.")
    @Test
    void 역이_존재하는지_확인() {
        Section section = new Section(LINE_1, 신도림역, 온수역, 7);

        boolean result = section.existsStation(신도림역);

        assertThat(result).isTrue();
    }

    @DisplayName("상행역이 서로 같은지 비교한다.")
    @Test
    void 상행역_비교() {
        Section section = new Section(LINE_1, 신도림역, 온수역, 7);
        Section otherSection = new Section(LINE_1, 신도림역, 부천역, 11);

        boolean result = section.isSameUpStation(otherSection);

        assertThat(result).isTrue();
    }

    @DisplayName("하행역이 서로 같은지 비교한다.")
    @Test
    void 하행역_비교() {
        Section section = new Section(LINE_1, 신도림역, 부천역, 7);
        Section otherSection = new Section(LINE_1, 온수역, 부천역, 5);

        boolean result = section.isSameDownStation(otherSection);

        assertThat(result).isTrue();
    }

    @DisplayName("상행역과 하행역이 모두 같은지 비교한다.")
    @Test
    void 상행역_하행역_비교() {
        Section section = new Section(LINE_1, 신도림역, 부천역, 7);
        Section otherSection = new Section(LINE_1, 신도림역, 부천역, 7);

        boolean result = section.isSameUpAndDownStation(otherSection);

        assertThat(result).isTrue();
    }

    @DisplayName("연결 가능한지 여부를 반환한다.")
    @Test
    void 연결_가능_여부() {
        Section section = new Section(LINE_1, 신도림역, 부천역, 7);
        Section otherSection = new Section(LINE_1, 부천역, 중동역, 4);

        boolean result = section.isConnect(otherSection);

        assertThat(result).isTrue();
    }

    @DisplayName("상행역과 길이를 변경한다.")
    @Test
    void 상행역_길이_변경() {
        Section section = new Section(LINE_1, 신도림역, 부천역, 7);
        Section otherSection = new Section(LINE_1, 신도림역, 온수역, 4);

        section.changeUpStation(otherSection);

        assertAll(
                () -> assertThat(section.getUpStation()).isEqualTo(온수역),
                () -> assertThat(section.getDistance()).isEqualTo(3)
        );
    }

    @DisplayName("하행역과 길이를 변경한다.")
    @Test
    void 하행역_길이_변경() {
        Section section = new Section(LINE_1, 신도림역, 부천역, 7);
        Section otherSection = new Section(LINE_1, 온수역, 부천역, 4);

        section.changeDownStation(otherSection);

        assertAll(
                () -> assertThat(section.getDownStation()).isEqualTo(부천역),
                () -> assertThat(section.getDistance()).isEqualTo(3)
        );
    }
}
