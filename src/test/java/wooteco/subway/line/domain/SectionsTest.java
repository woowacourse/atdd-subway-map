package wooteco.subway.line.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    private Section section;
    private Sections sections;

    @BeforeEach
    void setUp(){
        section = new Section(new Station(1L), new Station(2L), 10);
        sections = new Sections(Arrays.asList(section));
    }

//    @Test
//    @DisplayName("구간 리스트에 구간을 추가한다.")
//    void add() {
//        Section newSection = new Section(new Station(1L), new Station(4L), 10);
//        sections.add(newSection);
//
//        assertThat(sections.sections()).hasSize(2);
//    }

    @Test
    @DisplayName("노선에 역이 하나만 등록되어 있는지 확인한다. - 두개다 이미 등록되어 있음")
    void isOnlyOneRegistered1() {
        boolean isOnlyOneRegistered = sections.isOnlyOneRegistered(section);

        assertThat(isOnlyOneRegistered).isFalse();
    }

    @Test
    @DisplayName("노선에 역이 하나만 등록되어 있는지 확인한다. - 하나만 등록되어 있음")
    void isAlreadyRegistered2() {
        Section newSection = new Section(new Station(1L), new Station(3L), 10);
        boolean alreadyRegistered = sections.isOnlyOneRegistered(newSection);

        assertThat(alreadyRegistered).isTrue();
    }
}