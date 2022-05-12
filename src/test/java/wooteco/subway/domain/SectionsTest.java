package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("기존 구간과 상행역이 일치하는 새로운 구간을 추가하면 거리가 알맞게 조정이 된다.")
    void addDownStation() {
        Section section1 = new Section(1L, 1L, 3L, 10);
        Section section2 = new Section(1L, 1L, 2L, 3);
        Sections sections = new Sections(List.of(section1));
        Section addedSection = sections.add(section2);

        assertAll(
                () -> assertThat(addedSection.getUpStationId()).isEqualTo(2L),
                () -> assertThat(addedSection.getDownStationId()).isEqualTo(3L),
                () -> assertThat(addedSection.getDistance()).isEqualTo(7)
        );
    }

    @Test
    @DisplayName("기존 구간과 상행역이 일치하고 길이가 더 긴 새로운 구간을 추가하면 예외를 발생한다.")
    void addLongDownStation() {
        Section section1 = new Section(1L, 1L, 3L, 10);
        Section section2 = new Section(1L, 1L, 2L, 11);
        Sections sections = new Sections(List.of(section1));
        assertThatThrownBy(
                () -> sections.add(section2)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간보다 긴 역을 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("기존 구간과 하행역이 일치하는 새로운 구간을 추가하면 거리가 알맞게 조정이 된다.")
    void addUpStation() {
        Section section1 = new Section(1L, 1L, 3L, 10);
        Section section2 = new Section(1L, 2L, 3L, 6);
        Sections sections = new Sections(List.of(section1));
        Section addedSection = sections.add(section2);

        assertAll(
                () -> assertThat(addedSection.getUpStationId()).isEqualTo(1L),
                () -> assertThat(addedSection.getDownStationId()).isEqualTo(2L),
                () -> assertThat(addedSection.getDistance()).isEqualTo(4)
        );
    }

    @Test
    @DisplayName("기존 구간과 하행역이 일치하고 길이가 더 긴 새로운 구간을 추가하면 예외를 발생한다.")
    void addLongUpStation() {
        Section section1 = new Section(1L, 1L, 3L, 10);
        Section section2 = new Section(1L, 2L, 3L, 11);
        Sections sections = new Sections(List.of(section1));
        assertThatThrownBy(
                () -> sections.add(section2)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간보다 긴 역을 추가할 수 없습니다.");
    }
}
