package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("상행과 하행이 이미 등록된 경우 에러를 발생시킨다")
    void checkSectionErrorByAlreadyExist() {
        Section section = Section.of(1L, 2L, 10);
        Section section2 = Section.of(2L, 3L, 10);
        Sections sections = new Sections(List.of(section, section2));

        assertThatThrownBy(() -> sections.checkSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 모두 노선에 등록되어 있습니다.");
    }

    @Test
    @DisplayName("상행과 하행이 존재하지 않는 경우 에러를 발생시킨다")
    void checkSectionErrorByNotExist() {
        Section section = Section.of(1L, 2L, 10);
        Section section2 = Section.of(2L, 3L, 10);
        Section section3 = Section.of(5L, 4L, 10);
        Sections sections = new Sections(List.of(section, section2));

        assertThatThrownBy(() -> sections.checkSection(section3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 모두 노선에 등록되어 있지 않습니다.");
    }
}