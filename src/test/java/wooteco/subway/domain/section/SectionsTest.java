package wooteco.subway.domain.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.station.Station;

class SectionsTest {

    @DisplayName("빈 구간 리스트를 생성할 수 없습니다.")
    @Test
    void empty() {
        assertAll(
            () -> assertThatThrownBy(() -> new Sections(Collections.emptyList())).isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> new Sections(null)).isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("정렬되어 있지 않은 여러 구간을 통해 생성시 상행 종점부터 하행 종점까지 자동 정렬합니다. - 1")
    @Test
    void sort1() {
        // given
        Long lineId = 0L;
        Station a = new Station("a역");
        Station b = new Station("b역");
        Station c = new Station("c역");
        Station d = new Station("d역");
        Station e = new Station("e역");

        Section aToB = new Section(1L, lineId, a, b, 2);
        Section bToC = new Section(2L, lineId, b, c, 2);
        Section cToD = new Section(3L, lineId, c, d, 2);
        Section dToE = new Section(4L, lineId, d, e, 2);

        // when
        List<Section> sectionValues = Arrays.asList(dToE, bToC, cToD, aToB);
        Sections sections = new Sections(sectionValues);

        // then
//        sections.getValues().forEach(section -> System.out.print(section.getUpStation().getName() + " - " + section.getDownStation().getName() + ", "));
        assertThat(sections.getValues())
            .containsExactly(
                aToB, bToC, cToD, dToE
            );
    }

    @DisplayName("정렬되어 있지 않은 여러 구간을 통해 생성시 상행 종점부터 하행 종점까지 자동 정렬합니다. - 2")
    @Test
    void sort2() {
        // given
        Long lineId = 0L;
        Station a = new Station("a역");
        Station b = new Station("b역");
        Station c = new Station("c역");
        Station d = new Station("d역");
        Station e = new Station("e역");
        Station f = new Station("f역");

        Section aToB = new Section(1L, lineId, a, b, 2);
        Section bToC = new Section(2L, lineId, b, c, 2);
        Section cToD = new Section(3L, lineId, c, d, 2);
        Section dToE = new Section(4L, lineId, d, e, 2);
        Section etoF = new Section(5L, lineId, e, f, 2);

        // when
        List<Section> sectionValues = Arrays.asList(cToD, etoF, bToC, dToE, aToB);
        Sections sections = new Sections(sectionValues);

        // then
        assertThat(sections.getValues())
            .containsExactly(
                aToB, bToC, cToD, dToE, etoF
            );
    }

    @DisplayName("정렬되어 있지 않은 여러 구간을 통해 생성시 상행 종점부터 하행 종점까지 자동 정렬합니다. - 3")
    @Test
    void sort3() {
        // given
        Long lineId = 0L;
        Station a = new Station("a역");
        Station b = new Station("b역");

        Section aToB = new Section(1L, lineId, a, b, 2);

        // when
        List<Section> sectionValues = Arrays.asList(aToB);
        Sections sections = new Sections(sectionValues);

        // then
        assertThat(sections.getValues())
            .containsExactly(aToB);
    }
}