package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {
    private List<Section> sections = new LinkedList<>();

    @BeforeEach
    void setUp() {
        sections.add(Section.from(new Station("6"), new Station("8"), 5));
        sections.add(Section.from(new Station("2"), new Station("4"), 5));
        sections.add(Section.from(new Station("8"), new Station("10"), 5));
        sections.add(Section.from(new Station("4"), new Station("6"), 5));
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
}