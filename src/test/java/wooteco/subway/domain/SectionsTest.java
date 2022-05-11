package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    private Sections sections;

    @BeforeEach
    void setUp() {
        sections = new Sections(List.of(new Section(1L, 2L, 7)));
    }

    @DisplayName("Section의 상행선과 하행선이 둘다 일치할 때 예외가 발생한다.")
    @Test
    void validateSameSection() {
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 2L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동일한 Section은 추가할 수 없습니다.");
    }

    @DisplayName("Section의 상행선과 하행선이 현재 노선에 포함되지 않으면 예외가 발생한다.")
    @Test
    void validateNotExistStation() {
        assertThatThrownBy(() -> sections.addSection(new Section(3L, 4L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역, 하행역 둘 다 포함되지 않으면 추가할 수 없습니다.");
    }

    @DisplayName("Section이 추가될 때 기존 구간의 거리보다 멀면 예외가 발생한다.")
    @Test
    void validateDistance() {
        assertThatThrownBy(() -> sections.addSection(new Section(1L, 3L, 7)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새로 들어오는 구간의 거리가 추가될 구간보다 작아야 합니다.");
    }

    @DisplayName("Section에 새로운 구간을 추가한다.")
    @Test
    void addSection() {
        sections.addSection(new Section(1L, 3L, 2));
        sections.addSection(new Section(2L, 4L, 3));
        sections.addSection(new Section(5L, 4L, 1));
        sections.addSection(new Section(7L, 5L, 1));

        assertThat(sections.getSections()).containsExactly(new Section(1L, 3L, 2), new Section(3L, 2L, 5),
                new Section(2L, 7L, 2), new Section(5L, 4L, 1), new Section(7L, 5L, 1));
    }
}
