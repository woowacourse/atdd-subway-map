package wooteco.subway.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

@DisplayName("Sections 는")
class SectionsTest {

    @DisplayName("추가할 섹션의 위치를 찾고 섹션을 추가해야 한다.")
    @Test
    void add_Section() {
        final Sections sections = new Sections(new ArrayList<>());
        sections.addIfPossible(SectionFactory.from("ab3"));
        sections.addIfPossible(SectionFactory.from("ac3"));
        assertThat(sections).extracting("value").isEqualTo(List.of(SectionFactory.from("ac3"),
                SectionFactory.from("cb3")));
    }


    @DisplayName("추가할 섹션의 위치를 찾고 섹션을 추가해야 한다.")
    @Test
    void add_Section2() {
        final Sections sections = new Sections(new ArrayList<>());
        sections.addIfPossible(SectionFactory.from("ab3"));
        sections.addIfPossible(SectionFactory.from("cb3"));
        assertThat(sections).extracting("value").isEqualTo(List.of(
                SectionFactory.from("ac3"),SectionFactory.from("cb3")));
    }
}
