package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("같은 역간의 구간 생성 시 예외가 발생한다.")
    @Test
    void constructor_SameStation_ThrowsException() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 1L, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("거리가 1 미만인 구간 생성 시 예외가 발생한다.")
    @Test
    void constructor_InvalidDistance_ThrowsException() {
        assertThatThrownBy(() -> new Section(1L, 1L, 1L, 2L, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구간을 나눈다.")
    @Test
    void split() {
        Section section = new Section(1L, 1L, 1L, 2L, 10);
        List<Section> sections = section.split(new Section(2L, 1L, 1L, 3L, 5));

        assertAll(
                () -> assertThat(sections.size()).isEqualTo(2),
                () -> assertThat(sections.get(0).getUpStationId()).isEqualTo(1L),
                () -> assertThat(sections.get(0).getDownStationId()).isEqualTo(3L),
                () -> assertThat(sections.get(0).getDistance()).isEqualTo(5),
                () -> assertThat(sections.get(1).getUpStationId()).isEqualTo(3L),
                () -> assertThat(sections.get(1).getDownStationId()).isEqualTo(2L),
                () -> assertThat(sections.get(1).getDistance()).isEqualTo(5)
        );
    }

    @DisplayName("나누려는 새로운 구간의 길이가 기존 길이보다 클 경우 예외가 발생한다.")
    @Test
    void split_InvalidDistance_ThrowsException() {
        Section section = new Section(1L, 1L, 1L, 2L, 10);
        assertThatThrownBy(() -> section.split(new Section(2L, 1L, 1L, 3L, 15)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("두 구간을 합친다.")
    @Test
    void merge() {
        Section section = new Section(1L, 1L, 1L, 2L, 10);
        Section mergedSection = section.merge(new Section(2L, 1L, 2L, 3L, 10));

        assertThat(mergedSection.getDistance()).isEqualTo(20);
    }
}
