package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    @DisplayName("구간 거리가 0 이하인 경우, IllegalArgumentException이 발생한다.")
    void section_distance_zero() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 2L, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("두 구간을 합친다.")
    void realignSection() {
        Section section1 = new Section(1L, 1L, 2L, 100);
        Section section2 = new Section(1L, 2L, 3L, 50);

        Section newSection = section1.realignSection(section2);
        assertThat(newSection).isEqualTo(new Section(1L, 1L, 3L, 150));
    }
}
