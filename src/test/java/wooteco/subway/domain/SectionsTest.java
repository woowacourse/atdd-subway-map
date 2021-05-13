package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    void addSection() {
        final Sections sections = Sections.create();
        final Section section = Section.create(Station.create("강남"), Station.create("잠실"), 5);
        sections.addSection(section);
        assertThat(sections.isEmpty()).isFalse();
        assertThat(sections.value()).hasSize(1);
        assertThat(sections.value()).contains(section);
    }

    @Test
    void firstSection() {
        final Sections sections = Sections.create();
        final Section section1 = Section.create(Station.create("강남"), Station.create("잠실"), 5);
        final Section section2 = Section.create(Station.create("잠실"), Station.create("잠실새내"), 4);
        sections.addSection(section1);
        sections.addSection(section2);

        assertThat(sections.value()).hasSize(2);
        assertThat(sections.firstSection()).isEqualTo(section1);
    }

    @Test
    void asStations() {
        final Station 강남역 = Station.create(1L, "강남");
        final Station 잠실역 = Station.create(2L, "잠실");
        final Station 홍대입구역 = Station.create(3L, "홍대입구");

        final Sections sections = Sections.create();

        final Section section1 = Section.create(1L, 강남역, 잠실역, 4);
        final Section section2 = Section.create(2L, 홍대입구역, 강남역, 4);

        sections.addSection(section1);
        sections.addSection(section2);

        assertThat(sections.asStations()).extracting("name")
            .containsExactlyInAnyOrder("홍대입구", "강남", "잠실");
    }

    @Test
    void affectedSectionWhenInserting() {
        final Station 강남역 = Station.create(1L, "강남");
        final Station 잠실역 = Station.create(2L, "잠실");
        final Station 홍대입구역 = Station.create(3L, "홍대입구");

        final Sections sections = Sections.create();

        final Section section1 = Section.create(1L, 강남역, 잠실역, 4);
        final Section section2 = Section.create(2L, 강남역, 홍대입구역, 2);

        sections.addSection(section1);

        final Section section = sections.affectedSectionWhenInserting(section2).get();
        assertThat(section.getUpStation().getName())
            .isEqualTo("홍대입구");
        assertThat(section.getDownStation().getName())
            .isEqualTo("잠실");
    }

    @Test
    void affectedSectionWhenRemoving() {
        final Station 강남역 = Station.create(1L, "강남");
        final Station 잠실역 = Station.create(2L, "잠실");
        final Station 홍대입구역 = Station.create(3L, "홍대입구");

        final Sections sections = Sections.create();

        final Section section1 = Section.create(1L, 강남역, 잠실역, 4);
        final Section section2 = Section.create(2L, 홍대입구역, 강남역, 4);

        sections.addSection(section1);
        sections.addSection(section2);

        final Section section = sections.affectedSectionWhenRemoving(강남역.getId()).get();
        assertThat(section.getUpStation().getName()).isEqualTo(홍대입구역.getName());
        assertThat(section.getDownStation().getName()).isEqualTo(잠실역.getName());
    }
}