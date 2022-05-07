package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.NotSplittableSectionException;

public class SectionTest {

    @DisplayName("상행역과 동일한 경우 구간 분리")
    @Test
    void splitByUpStation() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 1L, 3L, 6);
        Section splitSection = section1.split(section2);

        assertThat(splitSection).isEqualTo(new Section(1L, 1L, 3L, 2L, 4));
    }

    @DisplayName("하행역과 동일한 경우 구간 분리")
    @Test
    void splitByDownStation() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 3L, 2L, 7);
        Section splitSection = section1.split(section2);

        assertThat(splitSection).isEqualTo(new Section(1L, 1L, 1L, 3L, 3));
    }

    @DisplayName("거리가 더 긴 구간으로 분리 시 예외 발생")
    @Test
    void splitByLongerDistanceSection() {
        Section section1 = new Section(1L, 1L, 1L, 2L, 10);
        Section section2 = new Section(2L, 1L, 3L, 2L, 10);
        assertThatThrownBy(() -> section1.split(section2))
            .isInstanceOf(NotSplittableSectionException.class);
    }

}
