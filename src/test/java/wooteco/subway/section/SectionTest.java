package wooteco.subway.section;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.section.SectionDuplicationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {
    @DisplayName("Section 객체를 생성한다.")
    @Test
    void createSection() {
        Section section = new Section(1L, 2L, 10);
        assertThat(section).isInstanceOf(Section.class);
    }

    @DisplayName("Section 객체를 생성할 때 상행역과 하행역이 중복되는 경우 예외가 발생한다.")
    @Test
    void createSectionDuplicationException() {
        assertThatThrownBy(() -> {
            new Section(1L, 1L, 10);
        }).isInstanceOf(SectionDuplicationException.class)
                .hasMessage("상행역과 하행역은 중복되지 않게 입력해주세요.");
    }
}