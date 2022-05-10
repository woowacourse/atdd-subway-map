package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SectionsTest {

    private static final Section section1 = new Section(1L, 1L, 3L, 1L, 10);
    private static final Section section2 = new Section(2L, 1L, 2L, 3L, 10);
    private static final Section section3 = new Section(3L, 1L, 4L, 2L, 10);
    private static final Section section4 = new Section(4L, 1L, 5L, 4L, 10);

    private Sections sections;

    @BeforeEach
    void init() {
        sections = new Sections(new ArrayList<>(List.of(section3, section2, section1, section4)));
    }

    @DisplayName("Section 추가 확인")
    @ParameterizedTest
    @MethodSource("parameterProvider")
    void add(Section newSection, Section updateSection) {
        // given
        List<Section> expected = List.of(newSection, updateSection);

        // when

        // then
        assertThat(sections.add(newSection)).isEqualTo(expected);
    }

    private static Stream<Arguments> parameterProvider() {
        return Stream.of(
                Arguments.arguments(
                        new Section(1L, 4L, 6L, 5),
                        new Section(3L, 1L, 6L, 2L, 5)
                ),
                Arguments.arguments(
                        new Section(1L, 6L, 4L, 5),
                        new Section(4L, 1L, 5L, 6L, 5)
                )
        );
    }

    @DisplayName("Section 삭제")
    @Test
    void delete() {
        // given
        Section expected = new Section(section2.getId(), section2.getLineId(), section3.getUpStationId(),
                section2.getDownStationId(), section2.getDistance() + section3.getDistance());

        // when
        Optional<Section> deletedSection = sections.delete(section3.getId());

        // then
        assertThat(deletedSection.get()).isEqualTo(expected);
    }
}