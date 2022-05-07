package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.LinkedList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("구간과 공유하는 역이 없으면 아무 관계도 아님")
    @Test
    void 공유하는_역이_없는_구간_NONE() {
        Section section = new Section(new Station("홍대입구역"), new Station("합정역"), 1);
        Sections sections = new Sections(section);

        Section target = new Section(new Station("용산역"), new Station("삼각지역"), 1);

        assertThat(sections.calculateRelation(target)).isEqualTo(Relation.NONE);
    }

    @DisplayName("상행 종착역이 구간의 하행역이면 연장 관계")
    @Test
    void 상행_종착역_구간_하행역_일치_EXTEND() {
        Section section = new Section(new Station("합정역"), new Station("홍대입구역"), 1);
        Sections sections = new Sections(section);

        Section target = new Section(new Station("당산역"), new Station("합정역"), 1);

        assertThat(sections.calculateRelation(target)).isEqualTo(Relation.EXTEND);
    }

    @DisplayName("하행 종착역이 구간의 상행역이면 연장 관계")
    @Test
    void 하행_종착역_구간_상행역_일치_EXTEND() {
        Section section = new Section(new Station("합정역"), new Station("홍대입구역"), 1);
        Sections sections = new Sections(section);

        Section target = new Section(new Station("홍대입구역"), new Station("신촌역"), 1);

        assertThat(sections.calculateRelation(target)).isEqualTo(Relation.EXTEND);
    }

    @DisplayName("구간과 같은 상행 역을 공유하면 나눠지는 관계")
    @Test
    void 같은_상행역_공유_DIVIDE() {
        Section section = new Section(new Station("합정역"), new Station("신촌역"), 1);
        Sections sections = new Sections(section);

        Section target = new Section(new Station("합정역"), new Station("홍대입구역"), 1);

        assertThat(sections.calculateRelation(target)).isEqualTo(Relation.DIVIDE);
    }

    @DisplayName("구간과 같은 하행 역을 공유하면 나눠지는 관계")
    @Test
    void 같은_하행역_공유_DIVIDE() {
        Section section = new Section(new Station("당산역"), new Station("홍대입구역"), 1);
        Sections sections = new Sections(section);

        Section target = new Section(new Station("합정역"), new Station("홍대입구역"), 1);

        assertThat(sections.calculateRelation(target)).isEqualTo(Relation.DIVIDE);
    }
    
    @DisplayName("구간의 두 역이 이미 존재하면 포함 관계")
    @Test
    void 두_역이_이미_존재_INCLUDE() {
        Section section = new Section(new Station("당산역"), new Station("홍대입구역"), 1);
        Sections sections = new Sections(section);

        Section target = new Section(new Station("홍대입구역"), new Station("당산역"), 1);

        assertThat(sections.calculateRelation(target)).isEqualTo(Relation.INCLUDE);
    }

    @DisplayName("추가하려는 구간의 두 역이 이미 존재하면 예외 발생")
    @Test
    void 구간_추가_두_역_이미_존재_예외발생() {
        Section section1 = new Section(new Station("당산역"), new Station("합정역"), 1);
        Section section2 = new Section(new Station("합정역"), new Station("홍대입구역"), 1);
        LinkedList<Section> listSections = new LinkedList<>();
        listSections.add(section1);
        listSections.add(section2);

        Sections sections = new Sections(listSections);
        Section target = new Section(new Station("당산역"), new Station("홍대입구역"), 1);

        assertThatThrownBy(() -> sections.add(target))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("추가하려는 구간과 겹치는 역이 없다면 예외 발생")
    @Test
    void 구간_추가_겹치는_역_없음_예외발생() {
        Section section1 = new Section(new Station("당산역"), new Station("합정역"), 1);
        Section section2 = new Section(new Station("합정역"), new Station("홍대입구역"), 1);
        LinkedList<Section> listSections = new LinkedList<>();
        listSections.add(section1);
        listSections.add(section2);

        Sections sections = new Sections(listSections);
        Section target = new Section(new Station("강남역"), new Station("선릉역"), 1);

        assertThatThrownBy(() -> sections.add(target))
                .isInstanceOf(IllegalArgumentException.class);

    }

}