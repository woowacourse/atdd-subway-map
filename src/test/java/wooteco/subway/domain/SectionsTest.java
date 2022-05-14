package wooteco.subway.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("Sections 는")
class SectionsTest {

    @DisplayName("추가할 섹션의 위치를 찾고 섹션을 추가해야 한다.")
    @ParameterizedTest(name = "{index} {displayName} section={0} expectedResult={1}")
    @MethodSource("provideAddSectionSource")
    void add_Section(final Section section, final List<Section> expectedResult) {
        final Sections sections = new Sections(new ArrayList<>());
        sections.addIfPossible(SectionFactory.from("ab3"));
        sections.addIfPossible(section);
        assertThat(sections).extracting("value").isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideAddSectionSource() {
        return Stream.of(
                Arguments.of(SectionFactory.from("ac2"), List.of(SectionFactory.from("ac2"),
                        SectionFactory.from("cb1"))),
                Arguments.of(SectionFactory.from ("cb2"), List.of(
                        SectionFactory.from("ac1"),SectionFactory.from("cb2"))),
                Arguments.of(SectionFactory.from("bc3"), List.of(
                        SectionFactory.from("ab3"), SectionFactory.from("bc3"))),
                Arguments.of(SectionFactory.from("ca3"), List.of(
                        SectionFactory.from("ca3"), SectionFactory.from("ab3")))
        );
    }

    @DisplayName("지울 역을 찾고 역을 지워야 한다.")
    @ParameterizedTest(name = "{index} {displayName} section={0} expectedResult={1}")
    @MethodSource("provideDeleteSectionSource")
    void delete_Section(final Section section, final Station targetStation, final List<Section> expectedResult) {
        final Sections sections = new Sections(new ArrayList<>());
        sections.addIfPossible(SectionFactory.from("ab3"));
        sections.addIfPossible(section);
        sections.deleteIfPossible(targetStation);
        assertThat(sections).extracting("value").isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideDeleteSectionSource() {
        return Stream.of(
                Arguments.of(SectionFactory.from("ac1"), StationFactory.from("c"),
                        List.of(SectionFactory.from("ab3"))),
                Arguments.of(SectionFactory.from ("ca3"), StationFactory.from("c"),
                        List.of(SectionFactory.from("ab3"))),
                Arguments.of(SectionFactory.from("bc3"), StationFactory.from("c"),
                        List.of(SectionFactory.from("ab3")))
        );
    }

    @DisplayName("지워진 역을 찾아 반환할 수 있어야 한다.")
    @Test
    void getDeletedSections() {
        final Sections sections = new Sections(new ArrayList<>());
        final Section ab3 = SectionFactory.from("ab3");
        final Section bc3 = SectionFactory.from("bc3");
        sections.addIfPossible(ab3);
        sections.addIfPossible(bc3);
        sections.deleteIfPossible(new Station(1L, "b"));
        final List<Section> deletedSections = sections.getDeletedSections(
                new ArrayList<>(List.of(ab3, bc3)));
        final List<Section> expectedResult = List.of(ab3, bc3);
        assertThat(deletedSections).isEqualTo(expectedResult);
    }

    @DisplayName("추가된 구간을 찾아 반환할 수 있어야 한다.")
    @Test
    void getAddSections() {
        final Sections sections = new Sections(new ArrayList<>());
        final Section ab3 = SectionFactory.from("ab3");
        final Section bc3 = SectionFactory.from("bc3");
        final Section cd3 = SectionFactory.from("cd3");
        sections.addIfPossible(ab3);
        sections.addIfPossible(bc3);
        sections.addIfPossible(cd3);
        final List<Section> addSections = sections.getAddSections(
                new ArrayList<>(List.of(ab3, bc3)));
        final List<Section> expectedResult = List.of(cd3);
        assertThat(addSections).isEqualTo(expectedResult);
    }
}
