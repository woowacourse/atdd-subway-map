package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("구간 일급 컬렉션에 새 구간을 추가한다.")
    void addSection() {
        final Station station1 = new Station(1L, "강남역");
        final Station station2 = new Station(2L, "역삼역");
        final Station station3 = new Station(3L, "선릉역");
        final Station station4 = new Station(4L, "삼성역");
        final Section section1 = new Section(1L, station1, station2, 10);
        final Sections sections = new Sections(section1);

        sections.addSection(station2, station3, 10);
        sections.addSection(station3, station4, 5);

        final Section section2 = new Section(2L, station2, station3, 10);
        final Section section3 = new Section(3L, station3, station4, 5);

        assertThat(sections.getValue()).isEqualTo(List.of(section1, section2, section3));
    }

}