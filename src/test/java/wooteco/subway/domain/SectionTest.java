package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    void updateUpStation() {
        final Station 강남역 = Station.create(1L, "강남역");
        final Station 홍대역 = Station.create(2L, "홍대역");
        final Station 잠실새내역 = Station.create(3L, "잠실새내역");

        final Section section1 = Section.create(강남역, 홍대역, 6);
        final Section section2 = Section.create(홍대역, 잠실새내역, 3);

        section1.updateUpStationFromDownStation(section2);

        assertThat(section1.isUpStation(강남역)).isFalse();
        assertThat(section1.isUpStation(잠실새내역)).isTrue();
    }

    @Test
    void updateDownStation() {
        final Station 강남역 = Station.create(1L, "강남역");
        final Station 홍대역 = Station.create(2L, "홍대역");
        final Station 잠실새내역 = Station.create(3L, "잠실새내역");

        final Section section1 = Section.create(강남역, 홍대역, 6);
        final Section section2 = Section.create(잠실새내역, 홍대역, 3);

        section1.updateDownStationFromUpStation(section2);

        assertThat(section1.isDownStation(홍대역)).isFalse();
        assertThat(section1.isDownStation(잠실새내역)).isTrue();
    }

    @Test
    void containsStation() {
        final Station 강남역 = Station.create(1L, "강남역");
        final Station 홍대역 = Station.create(2L, "홍대역");

        final Section section = Section.create(강남역, 홍대역, 6);

        assertThat(section.containsStation(강남역)).isTrue();
        assertThat(section.containsStation(홍대역)).isTrue();
    }

    @Test
    void combineSection() {
        final Station 강남역 = Station.create(1L, "강남역");
        final Station 홍대역 = Station.create(2L, "홍대역");
        final Station 잠실새내역 = Station.create(3L, "잠실새내역");

        final Section section1 = Section.create(강남역, 홍대역, 6);
        final Section section2 = Section.create(홍대역, 잠실새내역, 3);

        section1.combineSection(section2);
        assertThat(section1.getDownStation()).isEqualTo(잠실새내역);
    }
}