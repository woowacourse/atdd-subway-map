package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SectionsTest {

    @Test
    @DisplayName("상행 종점에 구간을 추가한다.")
    void addUpDestination() {
        //given
        Section section = new Section(new Station("역삼"), new Station("강남"), 5);
        Sections sections = new Sections(section);

        //when
        Section newSection = new Section(new Station("선릉"), new Station("역삼"), 5);
        sections.add(newSection);

        //then
        assertThat(sections.getUpDestination()).isEqualTo(new Station("선릉"));
    }
}
