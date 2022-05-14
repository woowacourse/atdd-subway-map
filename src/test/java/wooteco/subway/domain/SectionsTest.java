package wooteco.subway.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static wooteco.subway.domain.factory.SectionFactory.AB3;
import static wooteco.subway.domain.factory.SectionFactory.AC1;
import static wooteco.subway.domain.factory.SectionFactory.AC2;
import static wooteco.subway.domain.factory.SectionFactory.BC3;
import static wooteco.subway.domain.factory.SectionFactory.CA3;
import static wooteco.subway.domain.factory.SectionFactory.CB1;
import static wooteco.subway.domain.factory.SectionFactory.CB2;
import static wooteco.subway.domain.factory.SectionFactory.CD3;
import static wooteco.subway.domain.factory.StationFactory.C;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wooteco.subway.domain.factory.SectionFactory;
import wooteco.subway.domain.factory.StationFactory;

@DisplayName("Sections 는")
class SectionsTest {

    @DisplayName("추가할 섹션의 위치를 찾고 섹션을 추가해야 한다.")
    @ParameterizedTest(name = "{index} {displayName} section={0} expectedResult={1}")
    @MethodSource("provideAddSectionSource")
    void add_Section(final Section section, final List<Section> expectedResult) {
        final Sections sections = new Sections(new ArrayList<>());
        sections.addIfPossible(SectionFactory.from(AB3));
        sections.addIfPossible(section);
        assertThat(sections).extracting("value").isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideAddSectionSource() {
        return Stream.of(
                Arguments.of(SectionFactory.from(AC2), List.of(SectionFactory.from(AC2),
                        SectionFactory.from(CB1))),
                Arguments.of(SectionFactory.from (CB2), List.of(
                        SectionFactory.from(AC1),SectionFactory.from(CB2))),
                Arguments.of(SectionFactory.from(BC3), List.of(
                        SectionFactory.from(AB3), SectionFactory.from(BC3))),
                Arguments.of(SectionFactory.from(CA3), List.of(
                        SectionFactory.from(CA3), SectionFactory.from(AB3)))
        );
    }

    @DisplayName("지울 역을 찾고 역을 지워야 한다.")
    @ParameterizedTest(name = "{index} {displayName} section={0} expectedResult={1}")
    @MethodSource("provideDeleteSectionSource")
    void delete_Section(final Section section, final Station targetStation, final List<Section> expectedResult) {
        final Sections sections = new Sections(new ArrayList<>());
        sections.addIfPossible(SectionFactory.from(AB3));
        sections.addIfPossible(section);
        sections.deleteIfPossible(targetStation);
        assertThat(sections).extracting("value").isEqualTo(expectedResult);
    }

    private static Stream<Arguments> provideDeleteSectionSource() {
        return Stream.of(
                Arguments.of(SectionFactory.from(AC1), StationFactory.from(C),
                        List.of(SectionFactory.from(AB3))),
                Arguments.of(SectionFactory.from (CA3), StationFactory.from(C),
                        List.of(SectionFactory.from(AB3))),
                Arguments.of(SectionFactory.from(BC3), StationFactory.from(C),
                        List.of(SectionFactory.from(AB3)))
        );
    }

    @DisplayName("지워진 역을 찾아 반환할 수 있어야 한다.")
    @Test
    void getDeletedSections() {
        final Sections sections = new Sections(new ArrayList<>());
        final Section ab3 = SectionFactory.from(AB3);
        final Section bc3 = SectionFactory.from(BC3);
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
        final Section ab3 = SectionFactory.from(AB3);
        final Section bc3 = SectionFactory.from(BC3);
        final Section cd3 = SectionFactory.from(CD3);
        sections.addIfPossible(ab3);
        sections.addIfPossible(bc3);
        sections.addIfPossible(cd3);
        final List<Section> addSections = sections.getAddSections(
                new ArrayList<>(List.of(ab3, bc3)));
        final List<Section> expectedResult = List.of(cd3);
        assertThat(addSections).isEqualTo(expectedResult);
    }
}
