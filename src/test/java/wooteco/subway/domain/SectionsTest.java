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

    private final Section section1 = new Section(1L, 1L, 3L, 1L, 10);
    private final Section section2 = new Section(2L, 1L, 2L, 3L, 10);
    private final Section section3 = new Section(3L, 1L, 4L, 2L, 10);
    private final Section section4 = new Section(4L, 1L, 5L, 4L, 10);

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

        // when

        // then
        assertThat(sections.add(newSection).get()).isEqualTo(updateSection);
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

    @DisplayName("station id 목록")
    @Test
    void select_sorted_ids() {
        // given

        // when
        List<Long> ids = sections.getSortedStationIds();

        // then
        assertThat(ids).containsExactly(section1.getDownStationId(), section2.getDownStationId(),
                section3.getDownStationId(), section4.getDownStationId(), section4.getUpStationId());
    }
}