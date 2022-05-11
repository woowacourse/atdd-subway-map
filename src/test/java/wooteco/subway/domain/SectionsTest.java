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
        sections.add(Section.from(new Station("6"), new Station("8"), 5));
        sections.add(Section.from(new Station("2"), new Station("4"), 5));
        sections.add(Section.from(new Station("8"), new Station("10"), 5));
        sections.add(Section.from(new Station("4"), new Station("6"), 5));
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
                Section.from(new Station("2"), new Station("4"), 5),
                Section.from(new Station("4"), new Station("6"), 5),
                Section.from(new Station("6"), new Station("8"), 5),
                Section.from(new Station("8"), new Station("10"), 5));
    }

    @Test
    @DisplayName("sections의 맨 앞에 삽입되어야 한다.")
    void insertFirst() {
        Sections sections1 = Sections.of(sections);
        Section section = Section.from(new Station("1"), new Station("2"), 100);
        sections1.insert(section);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(5)
            .containsExactly(
                Section.from(new Station("1"), new Station("2"), 100),
                Section.from(new Station("2"), new Station("4"), 5),
                Section.from(new Station("4"), new Station("6"), 5),
                Section.from(new Station("6"), new Station("8"), 5),
                Section.from(new Station("8"), new Station("10"), 5));
    }

    @Test
    @DisplayName("4 -> 6 사이에 삽입되어 (4 -> 5) 구간과 (5 -> 6) 구간이 생겨야 한다. ")
    void insert1() {
        Sections sections1 = Sections.of(sections);
        Section section = Section.from(new Station("4"), new Station("5"), 3);
        sections1.insert(section);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(5)
            .containsExactly(
                Section.from(new Station("2"), new Station("4"), 5),
                Section.from(new Station("4"), new Station("5"), 3),
                Section.from(new Station("5"), new Station("6"), 2),
                Section.from(new Station("6"), new Station("8"), 5),
                Section.from(new Station("8"), new Station("10"), 5)
            );
    }

    @Test
    @DisplayName("4 -> 6 사이에 삽입되어 (4 -> 5) 구간과 (5 -> 6) 구간이 생겨야 한다. ")
    void insert2() {
        Sections sections1 = Sections.of(sections);
        Section section = Section.from(new Station("5"), new Station("6"), 3);
        sections1.insert(section);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(5)
            .containsExactly(
                Section.from(new Station("2"), new Station("4"), 5),
                Section.from(new Station("4"), new Station("5"), 2),
                Section.from(new Station("5"), new Station("6"), 3),
                Section.from(new Station("6"), new Station("8"), 5),
                Section.from(new Station("8"), new Station("10"), 5)
            );
    }

    @Test
    @DisplayName("맨 마지막에 삽입되어야 한다.")
    void insertLast() {
        Sections sections1 = Sections.of(sections);
        Section section = Section.from(new Station("10"), new Station("xx"), 100);
        sections1.insert(section);
        List<Section> newSections = sections1.getSections();
        assertThat(newSections)
            .hasSize(5)
            .containsExactly(
                Section.from(new Station("2"), new Station("4"), 5),
                Section.from(new Station("4"), new Station("6"), 5),
                Section.from(new Station("6"), new Station("8"), 5),
                Section.from(new Station("8"), new Station("10"), 5),
                Section.from(new Station("10"), new Station("xx"), 100));
    }

    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("invalidParameters")
    @DisplayName("들어갈 구간이 없으면 예외를 반환해야 한다.")
    void insertInvalidParameters(Section section, String testName) {
        Sections sections1 = Sections.of(sections);
        assertThatThrownBy(
            () -> sections1.insert(section)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> invalidParameters() {
        return Stream.of(
            Arguments.of(Section.from(new Station("11"), new Station("12"), 100), "아무 역과도 연결되지 않을 때"),
            Arguments.of(Section.from(new Station("2"), new Station("3"), 100), "역과는 연결되었지만 길이가 길때"),
            Arguments.of(Section.from(new Station("2"), new Station("4"), 3), "상행선과 하행선이 이미 있는 구간일 때")
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
                Section.from(new Station("4"), new Station("6"), 5),
                Section.from(new Station("6"), new Station("8"), 5),
                Section.from(new Station("8"), new Station("10"), 5));
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
                Section.from(new Station("2"), new Station("4"), 5),
                Section.from(new Station("4"), new Station("6"), 5),
                Section.from(new Station("6"), new Station("8"), 5)
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
                Section.from(new Station("2"), new Station("6"), 10),
                Section.from(new Station("6"), new Station("8"), 5),
                Section.from(new Station("8"), new Station("10"), 5)
            );
    }
}