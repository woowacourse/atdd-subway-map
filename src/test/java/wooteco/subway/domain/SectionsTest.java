package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.exception.SectionDuplicateException;

class SectionsTest {

    @Test
    @DisplayName("상행과 하행 모두 존재할 때 예외를 발생한다.")
    void duplicateSection() {
        final Section section = new Section(1L, 1L, new Station(1L, "신대방역"), new Station(2L, "선릉역"), 10);
        final Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.add(section))
                .isInstanceOf(SectionDuplicateException.class)
                .hasMessage("중복된 구간입니다.");
    }
}
