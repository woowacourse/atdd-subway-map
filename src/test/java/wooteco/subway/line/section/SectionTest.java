package wooteco.subway.line.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.exception.BiggerDistanceException;

class SectionTest {

    private static final Section SECTION = new Section(1L, 1L, 1L, 2L, 10);

    @DisplayName("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 예외가 발생한다.")
    @Test
    void validateSmaller() {
        assertThatThrownBy(() -> SECTION.validateSmaller(10))
            .isInstanceOf(BiggerDistanceException.class)
            .hasMessage("새로 추가할 구간의 거리가 기존 구간의 거리보다 크거나 같으면 안 됩니다.");
    }

    @DisplayName("중간에 역을 추가하면서 바꿔야할 구간을 가져온다.")
    @Test
    void createUpdatedSection() {
        Section updatedSection = SECTION.createUpdatedSection(1L, 3L, 3);
        assertThat(updatedSection).isEqualTo(new Section(1L, 1L, 3L, 2L, 7));

        updatedSection = SECTION.createUpdatedSection(3L, 2L, 3);
        assertThat(updatedSection).isEqualTo(new Section(1L, 1L, 1L, 3L, 7));
    }
}