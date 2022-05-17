package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SectionsTest {
    private List<Section> sections = new LinkedList<>();

    @BeforeEach
    void setUp() {
        sections.add(new Section(new Station("6"), new Station("8"), 5));
        sections.add(new Section(new Station("2"), new Station("4"), 5));
        sections.add(new Section(new Station("8"), new Station("10"), 5));
        sections.add(new Section(new Station("4"), new Station("6"), 5));
    }

    @AfterEach
    void setDown() {
        sections = new LinkedList<>();
    }

    @Test
    @DisplayName("Sections는 매개변수로 받는 List<Section>를 정렬해 가지고 있어야 한다.")
    void create() {
        Sections sections1 = Sections.of(sections);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(4)
            .containsExactly(
                new Section(new Station("2"), new Station("4"), 5),
                new Section(new Station("4"), new Station("6"), 5),
                new Section(new Station("6"), new Station("8"), 5),
                new Section(new Station("8"), new Station("10"), 5));
    }

    @Test
    @DisplayName("한 구간의 상행성과 하행선이 같으면 예외를 반환해야 한다.")
    void validateSameStation() {
        assertThatThrownBy(() -> new Section(new Station("a"), new Station("a"), 5))
            .hasMessage("구간의 상행선과 하행선이 같을 수 없습니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("sections의 맨 앞에 삽입되어야 한다.")
    void insertFirst() {
        Sections sections1 = Sections.of(sections);
        Section section = new Section(new Station("1"), new Station("2"), 100);
        sections1.insert(section);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(5)
            .containsExactly(
                new Section(new Station("1"), new Station("2"), 100),
                new Section(new Station("2"), new Station("4"), 5),
                new Section(new Station("4"), new Station("6"), 5),
                new Section(new Station("6"), new Station("8"), 5),
                new Section(new Station("8"), new Station("10"), 5));
    }

    @Test
    @DisplayName("4 -> 6 사이에 삽입되어 (4 -> 5) 구간과 (5 -> 6) 구간이 생겨야 한다. ")
    void insert1() {
        Sections sections1 = Sections.of(sections);
        Section section = new Section(new Station("4"), new Station("5"), 3);
        sections1.insert(section);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(5)
            .containsExactly(
                new Section(new Station("2"), new Station("4"), 5),
                new Section(new Station("4"), new Station("5"), 3),
                new Section(new Station("5"), new Station("6"), 2),
                new Section(new Station("6"), new Station("8"), 5),
                new Section(new Station("8"), new Station("10"), 5)
            );
    }

    @Test
    @DisplayName("4 -> 6 사이에 삽입되어 (4 -> 5) 구간과 (5 -> 6) 구간이 생겨야 한다. ")
    void insert2() {
        Sections sections1 = Sections.of(sections);
        Section section = new Section(new Station("5"), new Station("6"), 3);
        sections1.insert(section);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(5)
            .containsExactly(
                new Section(new Station("2"), new Station("4"), 5),
                new Section(new Station("4"), new Station("5"), 2),
                new Section(new Station("5"), new Station("6"), 3),
                new Section(new Station("6"), new Station("8"), 5),
                new Section(new Station("8"), new Station("10"), 5)
            );
    }

    @Test
    @DisplayName("맨 마지막에 삽입되어야 한다.")
    void insertLast() {
        Sections sections1 = Sections.of(sections);
        Section section = new Section(new Station("10"), new Station("xx"), 100);
        sections1.insert(section);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(5)
            .containsExactly(
                new Section(new Station("2"), new Station("4"), 5),
                new Section(new Station("4"), new Station("6"), 5),
                new Section(new Station("6"), new Station("8"), 5),
                new Section(new Station("8"), new Station("10"), 5),
                new Section(new Station("10"), new Station("xx"), 100));
    }

    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("exceptionParameters")
    @DisplayName("구간이 삽입될 수 없으면 예외를 반환해야 한다.")
    void insertExceptionParameters(Section section, String testName) {
        Sections sections1 = Sections.of(sections);
        assertThatThrownBy(() -> sections1.insert(section))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> exceptionParameters() {
        return Stream.of(
            Arguments.of(new Section(new Station("11"), new Station("12"), 100),
                "아무 역과도 연결되지 않을 때"),
            Arguments.of(new Section(new Station("2"), new Station("3"), 100),
                "역과는 연결되었지만 길이가 길때"),
            Arguments.of(new Section(new Station("2"), new Station("4"), 3),
                "상행선과 하행선이 이미 있는 구간일 때"),
            Arguments.of(new Section(new Station("2"), new Station("6"), 3),
                "상행선과 하행선이 이미 있는 구간일 때")
        );
    }

    @Test
    @DisplayName("맨 앞의 station이 삭제되어야 한다.")
    void deleteFirst() {
        Sections sections1 = Sections.of(sections);
        Station station = new Station("2");
        sections1.delete(station);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(3)
            .containsExactly(
                new Section(new Station("4"), new Station("6"), 5),
                new Section(new Station("6"), new Station("8"), 5),
                new Section(new Station("8"), new Station("10"), 5));
    }

    @Test
    @DisplayName("맨 마지막의 station이 삭제되어야 한다.")
    void deleteLast() {
        Sections sections1 = Sections.of(sections);
        Station station = new Station("10");
        sections1.delete(station);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(3)
            .containsExactly(
                new Section(new Station("2"), new Station("4"), 5),
                new Section(new Station("4"), new Station("6"), 5),
                new Section(new Station("6"), new Station("8"), 5)
            );
    }

    @Test
    @DisplayName("역 4를 삭제하면 4 -> 6 구간이 삭제되고 2 -> 6 구간의 길이가 10이 되어야 합니다.")
    void delete() {
        Sections sections1 = Sections.of(sections);
        Station station = new Station("4");
        sections1.delete(station);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(3)
            .containsExactly(
                new Section(new Station("2"), new Station("6"), 10),
                new Section(new Station("6"), new Station("8"), 5),
                new Section(new Station("8"), new Station("10"), 5)
            );
    }

    @Test
    @DisplayName("구간이 하나 남을 경우에 구간삭제를 시도하면 예외가 반환되어야 합니다.")
    void deleteLastSection() {
        Sections sections1 = Sections.of(sections);
        sections1.delete(new Station("2"));
        sections1.delete(new Station("4"));
        sections1.delete(new Station("6"));
        assertThatThrownBy(() -> sections1.delete(new Station("8")))
            .hasMessage("한개 남은 구간은 제거할 수 없습니다.")
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("없는 구간의 삭제를 시도하면 id는 -1이 반환되어야 합니다.")
    void deleteNoSuchSection() {
        Sections sections1 = Sections.of(sections);
        Station station = new Station("xx");
        assertThat(sections1.delete(station)).isEqualTo(-1L);
    }
}
