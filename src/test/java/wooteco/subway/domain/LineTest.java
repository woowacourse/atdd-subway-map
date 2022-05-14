package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @DisplayName("새로운 종점을 만드는 구간 추가")
    @Test
    void 구간_추가1() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoC, CtoD)));

        Section DtoE = new Section(new Station("D"), new Station("E"), 1);
        sections.add(DtoE);
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThat(result.get(2)).isEqualTo(CtoD),
                () -> assertThat(result.get(3)).isEqualTo(DtoE)
        );
    }

    @DisplayName("존재하는 구간을 나누는 구간 추가")
    @Test
    void 구간_추가2() {
        Section AtoC = new Section(new Station("A"), new Station("C"), 2);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoC, CtoD)));

        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        sections.add(AtoB);
        List<Section> result = sections.getSections();

        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThat(result.get(2)).isEqualTo(CtoD)
        );
    }

    @DisplayName("종점역 삭제")
    @Test
    void 종점역_삭제() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoC, CtoD)));

        sections.delete(new Station("D"));
        List<Section> result = sections.getSections();

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoC),
                () -> assertThatThrownBy(() -> result.get(2))
                        .isInstanceOf(IndexOutOfBoundsException.class)
        );
    }

    @DisplayName("중간역 삭제")
    @Test
    void 중간역_삭제() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoC, CtoD)));

        sections.delete(new Station("C"));
        List<Section> result = sections.getSections();

        Section BtoD = new Section(new Station("B"), new Station("D"), 2);
        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(AtoB),
                () -> assertThat(result.get(1)).isEqualTo(BtoD),
                () -> assertThatThrownBy(() -> result.get(2))
                        .isInstanceOf(IndexOutOfBoundsException.class)
        );
    }
    
    @DisplayName("중간역 삭제 후 변경된 구간 탐지")
    @Test
    void 두_라인_사이_다른_구간_반환() {
        Section AtoB = new Section(new Station("A"), new Station("B"), 1);
        Section BtoC = new Section(new Station("B"), new Station("C"), 1);
        Section CtoD = new Section(new Station("C"), new Station("D"), 1);
        Sections sections = new Sections(new LinkedList<>(List.of(AtoB, BtoC, CtoD)));
        Line line = new Line("1호선", "red", sections);
        Sections oldSections = new Sections(line.getSections());
        line.delete(new Station("C"));
        List<Section> result = oldSections.findDifferentSections(new Sections(line.getSections()));

        assertAll(
                () -> assertThat(result.get(0)).isEqualTo(BtoC),
                () -> assertThat(result.get(1)).isEqualTo(CtoD),
                () -> assertThatThrownBy(() -> result.get(2))
                        .isInstanceOf(IndexOutOfBoundsException.class)
        );
    }
}