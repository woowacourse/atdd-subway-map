package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.NotFoundException;

@SuppressWarnings("NonAsciiCharacters")
class SectionsFactoryTest {

    private final Station STATION1 = new Station(1L, "역1");
    private final Station STATION2 = new Station(2L, "역2");
    private final Station STATION3 = new Station(3L, "역3");
    private final Station STATION4 = new Station(4L, "역4");
    private final Station STATION5 = new Station(5L, "역5");

    private final Section UPPER_END_SECTION = new Section(STATION3, STATION4, 2);
    private final Section SECTION2 = new Section(STATION4, STATION5, 3);
    private final Section SECTION3 = new Section(STATION5, STATION2, 4);
    private final Section LOWER_END_SECTION = new Section(STATION2, STATION1, 5);

    @Test
    void generate_메서드는_상행종점부터_하행종점까지_정렬된_일급컬렉션을_생성하여_반환() {
        List<Section> sectionsInRandomOrder = List.of(SECTION3, LOWER_END_SECTION, SECTION2, UPPER_END_SECTION);

        Sections actual = SectionsFactory.generate(sectionsInRandomOrder);
        Sections expected = new Sections(
                List.of(UPPER_END_SECTION, SECTION2, SECTION3, LOWER_END_SECTION));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 비어있는_구간들의_리스트가_들어오면_예외발생() {
        assertThatThrownBy(() -> SectionsFactory.generate(List.of()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 연결되지_않은_구간들의_리스트가_들어오면_예외발생() {
        List<Section> middleSectionsMissing = List.of(UPPER_END_SECTION, LOWER_END_SECTION);

        assertThatThrownBy(() -> SectionsFactory.generate(middleSectionsMissing))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
