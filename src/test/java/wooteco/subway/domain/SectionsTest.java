package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SectionsTest {

    @DisplayName("Section 추가 확인")
    @ParameterizedTest
    @MethodSource("parameterProvider")
    void add(Section newSection, Section updateSection) {
        // given
        Section section1 = new Section(1L, 3L, 1L, 10);
        Section section2 = new Section(1L, 2L, 3L, 10);
        Section section3 = new Section(1L, 4L, 2L, 10);
        Section section4 = new Section(1L, 5L, 4L, 10);
        Sections sections = new Sections(new ArrayList<>(List.of(section3, section2, section1, section4)));

        List<Section> expected = List.of(newSection, updateSection);

        // when

        // then
        assertThat(sections.add(newSection)).isEqualTo(expected);
    }

    private static Stream<Arguments> parameterProvider() {
        return Stream.of(
                Arguments.arguments(
                        new Section(1L, 4L, 6L, 5),
                        new Section(1L, 6L, 2L, 5)
                ),
                Arguments.arguments(
                        new Section(1L, 6L, 4L, 5),
                        new Section(1L, 5L, 6L, 5)
                )
        );
    }
}