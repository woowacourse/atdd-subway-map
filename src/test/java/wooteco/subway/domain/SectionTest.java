package wooteco.subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Section")
class SectionTest {
    private final Station 강남역 = Station.create(1L,"강남역");
    private final Station 잠실역 = Station.create(2L,"잠실역");


    @Test
    @DisplayName("상행선이 같은지 확인")
    void isUpStation() {
        Section section1 = Section.create(강남역, 잠실역, 20);

        assertTrue(section1.isUpStation(강남역));
    }

    @Test
    @DisplayName("하행선이 같은지 확인")
    void isDownStation() {
        Section section1 = Section.create(강남역, 잠실역, 20);

        assertTrue(section1.isDownStation(잠실역));
    }

    @Test
    void updateUpStation() {
    }

    @Test
    void updateDownStation() {
    }

    @Test
    @DisplayName("같은역, 의미상 같은역 판단")
    void isSameSection() {
        Section section1 = Section.create( 강남역, 잠실역, 20);
        Section section1_1 = Section.create(강남역, 잠실역, 20);
        Section section_reversed = Section.create(잠실역, 강남역, 20);

        assertTrue(section1.isSameSection(section1_1));
        assertTrue(section1.isSameSection(section_reversed));
    }

}